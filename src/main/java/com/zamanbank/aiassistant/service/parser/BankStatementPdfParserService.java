package com.zamanbank.aiassistant.service.parser;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import com.zamanbank.aiassistant.dto.bank.BankTransaction;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankStatementPdfParserService {

  private static final String HALYK_BANK_PATTERN = "АО 'Народный Банк Казахстана'";
  private static final String CLIENT_NAME_PATTERN = "ФИО:";
  private static final String IIN_PATTERN = "ИИН:";
  private static final String ACCOUNT_PATTERN = "Текущий счет";
  private static final String BALANCE_PATTERN = "Входящий баланс:";
  private static final String OUTGOING_PATTERN = "Исходящий баланс:";

  public BankStatement parsePdfStatement(String filePath) throws IOException {
    log.info("Начинаем парсинг PDF файла: {}", filePath);

    try (PDDocument document = PDDocument.load(new File(filePath))) {
      PDFTextStripper stripper = new PDFTextStripper();
      String text = stripper.getText(document);

      log.info("Извлеченный текст из PDF (первые 2000 символов): {}", text.substring(0, Math.min(2000, text.length())));
      log.info("Общая длина извлеченного текста: {} символов", text.length());
      
      // Ищем строки с датами для отладки
      String[] lines = text.split("\n");
      int dateLines = 0;
      for (String line : lines) {
        if (line.matches(".*\\d{2}\\.\\d{2}\\.\\d{2}.*")) {
          dateLines++;
          if (dateLines <= 5) { // Показываем первые 5 строк с датами
            log.info("Найдена строка с датой: {}", line);
          }
        }
      }
      log.info("Найдено {} строк с датами", dateLines);

      return parseBankStatement(text);
    }
  }


  private BankStatement parseBankStatement(String text) {
    BankStatement.BankStatementBuilder builder = BankStatement.builder();

    log.info("Начинаем парсинг банковской выписки...");
    log.debug("Полный текст для парсинга: {}", text);

    // Парсим информацию о клиенте
    parseClientInfo(text, builder);

    // Парсим балансы
    parseBalances(text, builder);

    // Парсим транзакции
    List<BankTransaction> transactions = parseTransactions(text);
    builder.transactions(transactions);

    log.info("Успешно распарсено {} транзакций", transactions.size());

    return builder.build();
  }

  private void parseClientInfo(String text, BankStatement.BankStatementBuilder builder) {
    // Извлекаем ФИО
    String clientName = extractValueAfterPattern(text, CLIENT_NAME_PATTERN, "\n");
    builder.clientName(clientName);

    // Извлекаем ИИН
    String iin = extractValueAfterPattern(text, IIN_PATTERN, "\n");
    builder.iin(iin);

    // Извлекаем тип счета
    String accountType = extractValueAfterPattern(text, ACCOUNT_PATTERN, "\n");
    builder.accountType(accountType);

    log.debug("Клиент: {}, ИИН: {}, Тип счета: {}", clientName, iin, accountType);
  }

  private void parseBalances(String text, BankStatement.BankStatementBuilder builder) {
    // Извлекаем входящий баланс
    String incomingBalance = extractValueAfterPattern(text, BALANCE_PATTERN, "KZT");
    if (incomingBalance != null) {
      builder.incomingBalanceKzt(parseAmount(incomingBalance));
    }

    // Извлекаем исходящий баланс
    String outgoingBalance = extractValueAfterPattern(text, OUTGOING_PATTERN, "KZT");
    if (outgoingBalance != null) {
      builder.outgoingBalanceKzt(parseAmount(outgoingBalance));
    }

    log.debug("Входящий баланс: {}, Исходящий баланс: {}",
      builder.build().getIncomingBalanceKzt(),
      builder.build().getOutgoingBalanceKzt());
  }

  private List<BankTransaction> parseTransactions(String text) {
    List<BankTransaction> transactions = new ArrayList<>();

    log.info("Начинаем поиск транзакций в тексте...");
    
    // Находим таблицу транзакций
    String transactionTable = extractTransactionTable(text);
    
    if (transactionTable != null) {
      log.info("Найдена таблица транзакций: {}", transactionTable.substring(0, Math.min(200, transactionTable.length())));
      String[] lines = transactionTable.split("\n");
      log.info("Найдено {} строк в таблице транзакций", lines.length);

      for (String line : lines) {
        log.debug("Обрабатываем строку: {}", line);
        if (isTransactionLine(line)) {
          log.info("Найдена строка транзакции: {}", line);
          BankTransaction transaction = parseTransactionLine(line);
          if (transaction != null) {
            transactions.add(transaction);
            log.info("Добавлена транзакция: {}", transaction);
          }
        }
      }
    } else {
      log.warn("Таблица транзакций не найдена в тексте");
      // Попробуем найти транзакции по другим паттернам
      transactions = parseTransactionsByPattern(text);
    }

    return transactions;
  }

  private List<BankTransaction> parseTransactionsByPattern(String text) {
    List<BankTransaction> transactions = new ArrayList<>();
    
    log.info("Пытаемся найти транзакции по паттернам...");
    
    // Ищем строки, которые содержат дату и сумму
    String[] lines = text.split("\n");
    
    for (String line : lines) {
      // Ищем строки с датой в формате DD.MM.YYYY и суммой
      if (line.matches(".*\\d{2}\\.\\d{2}\\.\\d{4}.*\\d+.*")) {
        log.info("Найдена потенциальная транзакция: {}", line);
        BankTransaction transaction = parseTransactionLine(line);
        if (transaction != null) {
          transactions.add(transaction);
          log.info("Добавлена транзакция по паттерну: {}", transaction);
        }
      }
    }
    
    // Если не нашли транзакции обычным способом, попробуем более агрессивный поиск
    if (transactions.isEmpty()) {
      transactions = parseTransactionsAggressive(text);
    }
    
    log.info("Найдено {} транзакций по паттернам", transactions.size());
    return transactions;
  }

  private List<BankTransaction> parseTransactionsAggressive(String text) {
    List<BankTransaction> transactions = new ArrayList<>();
    
    log.info("Агрессивный поиск транзакций...");
    
    // Ищем все строки, которые содержат дату и сумму в любом формате
    String[] lines = text.split("\n");
    log.info("Обрабатываем {} строк текста", lines.length);
    
    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty()) continue;
      
      // Ищем строки с датой в формате DD.MM.YY (например: 04.10.25)
      if (line.matches(".*\\d{2}\\.\\d{2}\\.\\d{2}.*")) {
        log.debug("Проверяем строку с датой: {}", line);
        
        // Пытаемся найти сумму в строке (различные форматы)
        // Ищем паттерн: дата + сумма + ₸ + описание
        if (line.matches(".*\\d{2}\\.\\d{2}\\.\\d{2}.*[+-]?\\s*\\d+[,\\.]?\\d*\\s*₸.*")) {
          log.info("Найдена строка с датой и суммой: {}", line);
          BankTransaction transaction = parseTransactionLineFlexible(line);
          if (transaction != null) {
            transactions.add(transaction);
            log.info("Добавлена транзакция: {}", transaction);
          }
        }
      }
    }
    
    log.info("Агрессивный поиск нашел {} транзакций", transactions.size());
    return transactions;
  }

  private String extractTransactionTable(String text) {
    // Ищем начало таблицы транзакций
    String tableStart = "Дата проведения операции";
    String tableEnd = "Всего:";

    int startIndex = text.indexOf(tableStart);
    int endIndex = text.indexOf(tableEnd);

    if (startIndex != -1 && endIndex != -1) {
      return text.substring(startIndex, endIndex);
    }

    return null;
  }

  private boolean isTransactionLine(String line) {
    // Проверяем, что строка содержит дату и сумму
    boolean hasDate = line.matches(".*\\d{2}\\.\\d{2}\\.\\d{4}.*");
    boolean hasAmount = line.matches(".*\\d+[,\\.]\\d+.*");
    
    log.debug("Проверяем строку: {} - дата: {}, сумма: {}", line, hasDate, hasAmount);
    
    return hasDate && hasAmount;
  }

  private BankTransaction parseTransactionLine(String line) {
    try {
      log.debug("Парсим строку транзакции: {}", line);
      
      // Парсим строку транзакции
      String[] parts = line.split("\\s+");
      log.debug("Разделили на {} частей: {}", parts.length, java.util.Arrays.toString(parts));

      if (parts.length < 2) {
        log.warn("Слишком мало частей в строке: {}", line);
        return null;
      }

      // Извлекаем дату (первая часть должна содержать дату)
      LocalDate operationDate = null;
      int dateIndex = -1;
      for (int i = 0; i < parts.length; i++) {
        if (parts[i].matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
          operationDate = parseDate(parts[i]);
          dateIndex = i;
          break;
        }
      }
      
      if (operationDate == null) {
        log.warn("Не найдена дата в строке: {}", line);
        return null;
      }

      // Извлекаем сумму (последняя часть с числом)
      BigDecimal amount = null;
      int amountIndex = -1;
      for (int i = parts.length - 1; i >= 0; i--) {
        if (parts[i].matches("-?\\d+[,\\.]\\d+")) {
          amount = parseAmount(parts[i]);
          amountIndex = i;
          break;
        }
      }
      
      if (amount == null) {
        log.warn("Не найдена сумма в строке: {}", line);
        return null;
      }

      // Извлекаем описание (все части между датой и суммой)
      StringBuilder description = new StringBuilder();
      for (int i = dateIndex + 1; i < amountIndex; i++) {
        description.append(parts[i]).append(" ");
      }

      // Определяем тип транзакции
      TransactionType type = amount.compareTo(BigDecimal.ZERO) > 0
        ? TransactionType.INCOME
        : TransactionType.EXPENSE;

      BankTransaction transaction = BankTransaction.builder()
        .operationDate(operationDate)
        .processingDate(operationDate)
        .description(description.toString().trim())
        .amount(amount.abs())
        .currency("KZT")
        .type(type)
        .build();
        
      log.info("Успешно распарсена транзакция: {}", transaction);
      return transaction;

    } catch (Exception e) {
      log.warn("Ошибка при парсинге строки транзакции: {}", line, e);
      return null;
    }
  }

  private BankTransaction parseTransactionLineFlexible(String line) {
    try {
      log.debug("Гибкий парсинг строки транзакции: {}", line);

      // Ищем дату в формате DD.MM.YY (например: 04.10.25)
      java.util.regex.Pattern datePattern = java.util.regex.Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2})");
      java.util.regex.Matcher dateMatcher = datePattern.matcher(line);

      if (!dateMatcher.find()) {
        log.warn("Не найдена дата в строке: {}", line);
        return null;
      }

      LocalDate operationDate = parseDate(dateMatcher.group(1));

      // Ищем сумму с символом тенге (например: - 110,00 ₸ или + 2 000,00 ₸)
      java.util.regex.Pattern amountPattern = java.util.regex.Pattern.compile("([+-]?\\s*\\d+[,\\.]?\\d*)\\s*₸");
      java.util.regex.Matcher amountMatcher = amountPattern.matcher(line);

      BigDecimal amount = BigDecimal.ZERO;
      if (amountMatcher.find()) {
        amount = parseAmount(amountMatcher.group(1));
      } else {
        log.warn("Не найдена сумма в строке: {}", line);
        return null;
      }

      // Извлекаем описание - все после суммы с ₸
      String description = line.replaceAll("\\d{2}\\.\\d{2}\\.\\d{2}", "")
                                .replaceAll("[+-]?\\s*\\d+[,\\.]?\\d*\\s*₸", "")
                                .trim();

      // Очищаем описание от лишних символов
      description = description.replaceAll("\\s+", " ").trim();

      // Определяем тип транзакции
      TransactionType type = amount.compareTo(BigDecimal.ZERO) > 0
        ? TransactionType.INCOME
        : TransactionType.EXPENSE;

      BankTransaction transaction = BankTransaction.builder()
        .operationDate(operationDate)
        .processingDate(operationDate)
        .description(description)
        .amount(amount.abs())
        .currency("KZT")
        .type(type)
        .build();

      log.info("Гибко распарсена транзакция: {}", transaction);
      return transaction;

    } catch (Exception e) {
      log.warn("Ошибка при гибком парсинге строки транзакции: {}", line, e);
      return null;
    }
  }

  private String extractValueAfterPattern(String text, String pattern, String delimiter) {
    int patternIndex = text.indexOf(pattern);
    if (patternIndex != -1) {
      int startIndex = patternIndex + pattern.length();
      int endIndex = text.indexOf(delimiter, startIndex);
      if (endIndex == -1) {
        endIndex = text.indexOf("\n", startIndex);
      }
      if (endIndex != -1) {
        return text.substring(startIndex, endIndex).trim();
      }
    }
    return null;
  }

  private BigDecimal parseAmount(String amountStr) {
    if (amountStr == null || amountStr.trim().isEmpty()) {
      return BigDecimal.ZERO;
    }

    try {
      // Убираем пробелы и заменяем запятую на точку
      String cleanAmount = amountStr.replaceAll("\\s+", "").replace(",", ".");
      
      // Убираем лишние символы
      cleanAmount = cleanAmount.replaceAll("[^\\d.-]", "");
      
      // Проверяем, что у нас есть число
      if (cleanAmount.isEmpty() || cleanAmount.equals("-") || cleanAmount.equals(".")) {
        return BigDecimal.ZERO;
      }
      
      return new BigDecimal(cleanAmount);
    } catch (NumberFormatException e) {
      log.warn("Ошибка при парсинге суммы: {}", amountStr);
      return BigDecimal.ZERO;
    }
  }

  private LocalDate parseDate(String dateStr) {
    try {
      // Сначала пробуем формат DD.MM.YY
      if (dateStr.matches("\\d{2}\\.\\d{2}\\.\\d{2}")) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return LocalDate.parse(dateStr, formatter);
      }
      // Затем пробуем формат DD.MM.YYYY
      else if (dateStr.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(dateStr, formatter);
      }
      else {
        log.warn("Неизвестный формат даты: {}", dateStr);
        return LocalDate.now();
      }
    } catch (Exception e) {
      log.warn("Ошибка при парсинге даты: {}", dateStr);
      return LocalDate.now();
    }
  }
}
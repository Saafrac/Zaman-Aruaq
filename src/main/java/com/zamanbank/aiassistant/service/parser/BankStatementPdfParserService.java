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

      log.debug("Извлеченный текст из PDF: {}", text.substring(0, Math.min(500, text.length())));

      return parseBankStatement(text);
    }
  }

  private BankStatement parseBankStatement(String text) {
    BankStatement.BankStatementBuilder builder = BankStatement.builder();

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

    // Находим таблицу транзакций
    String transactionTable = extractTransactionTable(text);

    if (transactionTable != null) {
      String[] lines = transactionTable.split("\n");

      for (String line : lines) {
        if (isTransactionLine(line)) {
          BankTransaction transaction = parseTransactionLine(line);
          if (transaction != null) {
            transactions.add(transaction);
          }
        }
      }
    }

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
    return line.matches("\\d{2}\\.\\d{2}\\.\\d{4}.*\\d+,\\d+");
  }

  private BankTransaction parseTransactionLine(String line) {
    try {
      // Парсим строку транзакции
      String[] parts = line.split("\\s+");

      if (parts.length < 4) {
        return null;
      }

      // Извлекаем дату
      LocalDate operationDate = parseDate(parts[0]);

      // Извлекаем описание (собираем все части кроме даты и суммы)
      StringBuilder description = new StringBuilder();
      for (int i = 1; i < parts.length - 1; i++) {
        if (!parts[i].matches("-?\\d+,\\d+")) {
          description.append(parts[i]).append(" ");
        }
      }

      // Извлекаем сумму
      BigDecimal amount = parseAmount(parts[parts.length - 1]);

      // Определяем тип транзакции
      TransactionType type = amount.compareTo(BigDecimal.ZERO) > 0
        ? TransactionType.INCOME
        : TransactionType.EXPENSE;

      return BankTransaction.builder()
        .operationDate(operationDate)
        .processingDate(operationDate)
        .description(description.toString().trim())
        .amount(amount.abs())
        .currency("KZT")
        .type(type)
        .build();

    } catch (Exception e) {
      log.warn("Ошибка при парсинге строки транзакции: {}", line, e);
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
      return new BigDecimal(cleanAmount);
    } catch (NumberFormatException e) {
      log.warn("Ошибка при парсинге суммы: {}", amountStr);
      return BigDecimal.ZERO;
    }
  }

  private LocalDate parseDate(String dateStr) {
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      return LocalDate.parse(dateStr, formatter);
    } catch (Exception e) {
      log.warn("Ошибка при парсинге даты: {}", dateStr);
      return LocalDate.now();
    }
  }
}
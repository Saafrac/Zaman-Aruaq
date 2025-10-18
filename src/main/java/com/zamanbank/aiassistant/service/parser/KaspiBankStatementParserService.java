package com.zamanbank.aiassistant.service.parser;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import com.zamanbank.aiassistant.dto.bank.BankTransaction;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class KaspiBankStatementParserService {

    private static final Pattern TRANSACTION_PATTERN = Pattern.compile(
        "(\\d{2}\\.\\d{2}\\.\\d{2})\\s+" +  // Дата
        "([+-]?\\s*\\d+[,\\.]?\\d*)\\s*₸\\s*" +  // Сумма с символом тенге
        "([А-Яа-я\\s\\.,\\-]+)"  // Описание
    );

    private static final Pattern DETAILED_TRANSACTION_PATTERN = Pattern.compile(
        "(\\d{2}\\.\\d{2}\\.\\d{2})\\s+" +  // Дата
        "([+-]?\\s*\\d+[,\\.]?\\d*)\\s*₸\\s*" +  // Сумма
        "([А-Яа-я\\s\\.,\\-]+)\\s*" +  // Тип операции
        "([А-Яа-я\\s\\.,\\-]+)"  // Детали
    );

    public BankStatement parseKaspiStatement(String text) {
        log.info("Начинаем парсинг выписки Kaspi Bank...");
        log.debug("Текст для парсинга: {}", text.substring(0, Math.min(500, text.length())));

        BankStatement.BankStatementBuilder builder = BankStatement.builder();

        // Парсим информацию о клиенте
        parseClientInfo(text, builder);

        // Парсим транзакции
        List<BankTransaction> transactions = parseTransactions(text);
        builder.transactions(transactions);

        log.info("Успешно распарсено {} транзакций Kaspi Bank", transactions.size());

        return builder.build();
    }

    private void parseClientInfo(String text, BankStatement.BankStatementBuilder builder) {
        // Извлекаем имя клиента
        Pattern namePattern = Pattern.compile("([А-Яа-я]+\\s+[А-Яа-я]+\\s+[А-Яа-я]+)");
        Matcher nameMatcher = namePattern.matcher(text);
        if (nameMatcher.find()) {
            builder.clientName(nameMatcher.group(1));
        }

        // Извлекаем номер карты
        Pattern cardPattern = Pattern.compile("\\*\\d{4}");
        Matcher cardMatcher = cardPattern.matcher(text);
        if (cardMatcher.find()) {
            builder.accountType("Kaspi Gold " + cardMatcher.group(0));
        }

        // Извлекаем номер счета
        Pattern accountPattern = Pattern.compile("KZ\\d+");
        Matcher accountMatcher = accountPattern.matcher(text);
        if (accountMatcher.find()) {
            builder.kztAccount(accountMatcher.group(0));
        }

        log.debug("Клиент: {}, Карта: {}, Счет: {}", 
            builder.build().getClientName(),
            builder.build().getAccountType(),
            builder.build().getKztAccount());
    }

    private List<BankTransaction> parseTransactions(String text) {
        List<BankTransaction> transactions = new ArrayList<>();
        
        log.info("Начинаем поиск транзакций Kaspi Bank...");
        
        // Разбиваем текст на строки
        String[] lines = text.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            
            // Ищем строки с датой и суммой в формате Kaspi Bank
            if (line.matches(".*\\d{2}\\.\\d{2}\\.\\d{2}.*[+-]?\\s*\\d+.*₸.*")) {
                log.debug("Найдена потенциальная транзакция Kaspi: {}", line);
                
                // Пытаемся найти описание в следующих строках
                String description = "";
                if (i + 1 < lines.length && !lines[i + 1].trim().matches(".*\\d{2}\\.\\d{2}\\.\\d{2}.*")) {
                    description = lines[i + 1].trim();
                }
                if (i + 2 < lines.length && !lines[i + 2].trim().matches(".*\\d{2}\\.\\d{2}\\.\\d{2}.*") && 
                    !lines[i + 2].trim().matches(".*[+-]?\\s*\\d+.*₸.*")) {
                    description += " " + lines[i + 2].trim();
                }
                
                BankTransaction transaction = parseTransactionLine(line, description);
                if (transaction != null) {
                    transactions.add(transaction);
                    log.info("Добавлена транзакция Kaspi: {}", transaction);
                }
            }
            
            // Также ищем строки в формате "дата сумма тип описание"
            if (line.matches("\\d{2}\\.\\d{2}\\.\\d{2}\\s+[+-]?\\s*\\d+.*₸\\s+\\w+\\s+.*")) {
                log.debug("Найдена транзакция в формате таблицы Kaspi: {}", line);
                BankTransaction transaction = parseTransactionLine(line, "");
                if (transaction != null) {
                    transactions.add(transaction);
                    log.info("Добавлена транзакция из таблицы Kaspi: {}", transaction);
                }
            }
        }
        
        log.info("Найдено {} транзакций Kaspi Bank", transactions.size());
        return transactions;
    }

    private BankTransaction parseTransactionLine(String line, String additionalDescription) {
        try {
            log.debug("Парсим строку транзакции Kaspi: {}", line);
            
            // Ищем дату в формате DD.MM.YY
            Pattern datePattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2})");
            Matcher dateMatcher = datePattern.matcher(line);
            
            if (!dateMatcher.find()) {
                log.warn("Не найдена дата в строке: {}", line);
                return null;
            }
            
            LocalDate operationDate = parseDate(dateMatcher.group(1));
            
            // Ищем сумму с символом тенге
            Pattern amountPattern = Pattern.compile("([+-]?\\s*\\d+[,\\.]?\\d*)\\s*₸");
            Matcher amountMatcher = amountPattern.matcher(line);
            
            BigDecimal amount = BigDecimal.ZERO;
            if (amountMatcher.find()) {
                amount = parseAmount(amountMatcher.group(1));
            } else {
                log.warn("Не найдена сумма в строке: {}", line);
                return null;
            }
            
            // Извлекаем описание - все после суммы
            String description = line.replaceAll("\\d{2}\\.\\d{2}\\.\\d{2}", "")
                                  .replaceAll("[+-]?\\s*\\d+[,\\.]?\\d*\\s*₸", "")
                                  .trim();
            
            // Добавляем дополнительное описание
            if (additionalDescription != null && !additionalDescription.isEmpty()) {
                description += " " + additionalDescription;
            }
            
            // Очищаем описание
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
                
            log.info("Успешно распарсена транзакция Kaspi: {}", transaction);
            return transaction;

        } catch (Exception e) {
            log.warn("Ошибка при парсинге строки транзакции Kaspi: {}", line, e);
            return null;
        }
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
            log.warn("Ошибка при парсинге суммы Kaspi: {}", amountStr);
            return BigDecimal.ZERO;
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            // Добавляем 20 к году, если он двузначный
            if (dateStr.matches("\\d{2}\\.\\d{2}\\.\\d{2}")) {
                String[] parts = dateStr.split("\\.");
                if (parts[2].length() == 2) {
                    parts[2] = "20" + parts[2];
                }
                dateStr = parts[0] + "." + parts[1] + "." + parts[2];
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            log.warn("Ошибка при парсинге даты Kaspi: {}", dateStr);
            return LocalDate.now();
        }
    }
}

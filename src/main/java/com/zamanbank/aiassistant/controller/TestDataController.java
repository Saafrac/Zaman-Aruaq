package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.enums.TransactionCategory;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import com.zamanbank.aiassistant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestDataController {

    private final TransactionRepository transactionRepository;

    @PostMapping("/data/user/{userId}")
    public ResponseEntity<Map<String, Object>> addTestData(@PathVariable UUID userId) {
        try {
            // Clear existing transactions for this user
            transactionRepository.deleteByUserId(userId);
            
            // Add test transactions
            Transaction[] testTransactions = {
                new Transaction(userId, new BigDecimal("50000.00"), "Зарплата", LocalDate.of(2025, 10, 1), TransactionType.INCOME, TransactionCategory.SALARY),
                new Transaction(userId, new BigDecimal("15000.00"), "Дополнительный доход", LocalDate.of(2025, 10, 5), TransactionType.INCOME, TransactionCategory.BUSINESS_INCOME),
                new Transaction(userId, new BigDecimal("5000.00"), "Инвестиции", LocalDate.of(2025, 10, 10), TransactionType.INCOME, TransactionCategory.INVESTMENT_RETURN),
                new Transaction(userId, new BigDecimal("8000.00"), "Продукты", LocalDate.of(2025, 10, 2), TransactionType.EXPENSE, TransactionCategory.FOOD),
                new Transaction(userId, new BigDecimal("3000.00"), "Транспорт", LocalDate.of(2025, 10, 3), TransactionType.EXPENSE, TransactionCategory.TRANSPORT),
                new Transaction(userId, new BigDecimal("2000.00"), "Медицина", LocalDate.of(2025, 10, 4), TransactionType.EXPENSE, TransactionCategory.HEALTHCARE),
                new Transaction(userId, new BigDecimal("5000.00"), "Развлечения", LocalDate.of(2025, 10, 6), TransactionType.EXPENSE, TransactionCategory.ENTERTAINMENT),
                new Transaction(userId, new BigDecimal("12000.00"), "Покупки", LocalDate.of(2025, 10, 7), TransactionType.EXPENSE, TransactionCategory.SHOPPING),
                new Transaction(userId, new BigDecimal("4000.00"), "Коммунальные услуги", LocalDate.of(2025, 10, 8), TransactionType.EXPENSE, TransactionCategory.UTILITIES),
                new Transaction(userId, new BigDecimal("25000.00"), "Прочие расходы", LocalDate.of(2025, 10, 9), TransactionType.EXPENSE, TransactionCategory.OTHER_EXPENSE)
            };
            
            for (Transaction transaction : testTransactions) {
                transactionRepository.save(transaction);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test data added successfully");
            response.put("transactionsCount", testTransactions.length);
            response.put("userId", userId.toString());
            
            log.info("Added {} test transactions for user {}", testTransactions.length, userId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error adding test data for user {}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error adding test data: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

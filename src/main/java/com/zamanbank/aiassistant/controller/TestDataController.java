package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.TransactionCategory;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import com.zamanbank.aiassistant.repository.TransactionRepository;
import com.zamanbank.aiassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestDataController {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @PostMapping("/data/user/{userId}")
    public ResponseEntity<Map<String, Object>> addTestData(@PathVariable UUID userId) {
        try {
            // Find user by ID
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found with ID: " + userId);
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            // Clear existing transactions for this user
            List<Transaction> existingTransactions = transactionRepository.findByUserId(userId);
            transactionRepository.deleteAll(existingTransactions);
            
            // Add test transactions
            Transaction[] testTransactions = {
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("50000.00"))
                    .type(TransactionType.INCOME)
                    .category(TransactionCategory.SALARY)
                    .description("Зарплата")
                    .date(LocalDate.of(2025, 10, 1))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("15000.00"))
                    .type(TransactionType.INCOME)
                    .category(TransactionCategory.BUSINESS_INCOME)
                    .description("Дополнительный доход")
                    .date(LocalDate.of(2025, 10, 5))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("5000.00"))
                    .type(TransactionType.INCOME)
                    .category(TransactionCategory.INVESTMENT_RETURN)
                    .description("Инвестиции")
                    .date(LocalDate.of(2025, 10, 10))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("8000.00"))
                    .type(TransactionType.EXPENSE)
                    .category(TransactionCategory.FOOD)
                    .description("Продукты")
                    .date(LocalDate.of(2025, 10, 2))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("3000.00"))
                    .type(TransactionType.EXPENSE)
                    .category(TransactionCategory.TRANSPORT)
                    .description("Транспорт")
                    .date(LocalDate.of(2025, 10, 3))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("2000.00"))
                    .type(TransactionType.EXPENSE)
                    .category(TransactionCategory.HEALTHCARE)
                    .description("Медицина")
                    .date(LocalDate.of(2025, 10, 4))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("5000.00"))
                    .type(TransactionType.EXPENSE)
                    .category(TransactionCategory.ENTERTAINMENT)
                    .description("Развлечения")
                    .date(LocalDate.of(2025, 10, 6))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("12000.00"))
                    .type(TransactionType.EXPENSE)
                    .category(TransactionCategory.SHOPPING)
                    .description("Покупки")
                    .date(LocalDate.of(2025, 10, 7))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("4000.00"))
                    .type(TransactionType.EXPENSE)
                    .category(TransactionCategory.UTILITIES)
                    .description("Коммунальные услуги")
                    .date(LocalDate.of(2025, 10, 8))
                    .build(),
                Transaction.builder()
                    .user(user)
                    .amount(new BigDecimal("25000.00"))
                    .type(TransactionType.EXPENSE)
                    .category(TransactionCategory.OTHER_EXPENSE)
                    .description("Прочие расходы")
                    .date(LocalDate.of(2025, 10, 9))
                    .build()
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
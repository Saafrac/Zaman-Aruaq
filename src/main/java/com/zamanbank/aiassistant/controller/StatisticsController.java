package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.repository.TransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final TransactionRepository transactionRepository;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить статистику пользователя")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable UUID userId) {
        try {
            List<Transaction> transactions = transactionRepository.findByUserId(userId);
            
            // Подсчитываем статистику
            long transactionsCount = transactions.size();
            
            BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType().toString().equals("INCOME"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType().toString().equals("EXPENSE"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Определяем период (берем даты из транзакций)
            LocalDate periodFrom = transactions.stream()
                .map(Transaction::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
            
            LocalDate periodTo = transactions.stream()
                .map(Transaction::getDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
            
            Map<String, Object> response = new HashMap<>();
            response.put("statementPeriod", periodFrom.toString() + " - " + periodTo.toString());
            response.put("totalIncome", totalIncome.doubleValue());
            response.put("totalExpenses", totalExpenses.doubleValue());
            response.put("message", "Выписка успешно обработана");
            response.put("transactionsCount", transactionsCount);
            response.put("netWorth", totalIncome.subtract(totalExpenses).doubleValue());
            
            log.info("Статистика для пользователя {}: {} транзакций, доход: {}, расходы: {}", 
                userId, transactionsCount, totalIncome, totalExpenses);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при получении статистики пользователя {}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Ошибка при получении статистики: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/overview")
    @Operation(summary = "Получить общую статистику")
    public ResponseEntity<Map<String, Object>> getOverviewStatistics() {
        try {
            List<Transaction> allTransactions = transactionRepository.findAll();
            
            long totalTransactions = allTransactions.size();
            
            BigDecimal totalIncome = allTransactions.stream()
                .filter(t -> t.getType().toString().equals("INCOME"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalExpenses = allTransactions.stream()
                .filter(t -> t.getType().toString().equals("EXPENSE"))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalTransactions", totalTransactions);
            response.put("totalIncome", totalIncome.doubleValue());
            response.put("totalExpenses", totalExpenses.doubleValue());
            response.put("netWorth", totalIncome.subtract(totalExpenses).doubleValue());
            response.put("message", "Общая статистика системы");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при получении общей статистики", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Ошибка при получении статистики: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/categories/user/{userId}")
    @Operation(summary = "Получить статистику по категориям для пользователя")
    public ResponseEntity<Map<String, Object>> getUserCategoryStatistics(@PathVariable UUID userId) {
        try {
            List<Transaction> transactions = transactionRepository.findByUserId(userId);
            
            // Группируем транзакции по категориям
            Map<String, CategoryStats> categoryStats = new HashMap<>();
            
            for (Transaction transaction : transactions) {
                String category = transaction.getCategory().toString();
                
                categoryStats.computeIfAbsent(category, k -> new CategoryStats())
                    .addTransaction(transaction);
            }
            
            // Подготавливаем данные для фронтенда
            List<Map<String, Object>> categoriesData = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (Map.Entry<String, CategoryStats> entry : categoryStats.entrySet()) {
                String category = entry.getKey();
                CategoryStats stats = entry.getValue();
                
                Map<String, Object> categoryData = new HashMap<>();
                categoryData.put("category", category);
                categoryData.put("categoryName", getCategoryDisplayName(category));
                categoryData.put("totalAmount", stats.getTotalAmount().doubleValue());
                categoryData.put("transactionCount", stats.getTransactionCount());
                categoryData.put("averageAmount", stats.getAverageAmount().doubleValue());
                categoryData.put("percentage", 0.0); // Будет вычислено позже
                
                categoriesData.add(categoryData);
                totalAmount = totalAmount.add(stats.getTotalAmount());
            }
            
            // Вычисляем проценты
            for (Map<String, Object> categoryData : categoriesData) {
                double amount = (Double) categoryData.get("totalAmount");
                double percentage = totalAmount.doubleValue() > 0 ? 
                    (amount / totalAmount.doubleValue()) * 100 : 0;
                categoryData.put("percentage", Math.round(percentage * 100.0) / 100.0);
            }
            
            // Сортируем по сумме (убывание)
            categoriesData.sort((a, b) -> 
                Double.compare((Double) b.get("totalAmount"), (Double) a.get("totalAmount")));
            
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categoriesData);
            response.put("totalAmount", totalAmount.doubleValue());
            response.put("totalTransactions", transactions.size());
            response.put("message", "Статистика по категориям успешно получена");
            
            log.info("Статистика по категориям для пользователя {}: {} категорий, общая сумма: {}", 
                userId, categoryStats.size(), totalAmount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при получении статистики по категориям пользователя {}", userId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Ошибка при получении статистики: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Вспомогательный класс для статистики категории
    private static class CategoryStats {
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private int transactionCount = 0;
        
        public void addTransaction(Transaction transaction) {
            totalAmount = totalAmount.add(transaction.getAmount());
            transactionCount++;
        }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public int getTransactionCount() { return transactionCount; }
        public BigDecimal getAverageAmount() { 
            return transactionCount > 0 ? totalAmount.divide(BigDecimal.valueOf(transactionCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
    }

    // Метод для получения отображаемого имени категории
    private String getCategoryDisplayName(String category) {
        switch (category) {
            case "FOOD": return "Еда и напитки";
            case "TRANSPORT": return "Транспорт";
            case "HEALTHCARE": return "Здоровье";
            case "EDUCATION": return "Образование";
            case "ENTERTAINMENT": return "Развлечения";
            case "SHOPPING": return "Покупки";
            case "UTILITIES": return "Коммунальные услуги";
            case "INSURANCE": return "Страхование";
            case "INVESTMENT_RETURN": return "Доход от инвестиций";
            case "SALARY": return "Зарплата";
            case "BUSINESS_INCOME": return "Доход от бизнеса";
            case "RENTAL_INCOME": return "Доход от аренды";
            case "OTHER_INCOME": return "Прочие доходы";
            case "SUBSCRIPTIONS": return "Подписки";
            case "TRAVEL": return "Путешествия";
            case "CLOTHING": return "Одежда";
            case "HOME_MAINTENANCE": return "Ремонт дома";
            case "OTHER_EXPENSE": return "Прочие расходы";
            default: return category;
        }
    }
}

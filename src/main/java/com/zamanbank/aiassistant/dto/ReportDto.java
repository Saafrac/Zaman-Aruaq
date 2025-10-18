package com.zamanbank.aiassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private String reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netIncome;
    private BigDecimal savingsRate;
    private Map<String, BigDecimal> expensesByCategory;
    private List<TransactionSummary> topTransactions;
    private List<GoalProgress> goalProgress;
    private String summary;
    private LocalDate generatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionSummary {
        private Long id;
        private String description;
        private BigDecimal amount;
        private String category;
        private LocalDate date;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalProgress {
        private Long goalId;
        private String title;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private Double progressPercentage;
        private LocalDate targetDate;
    }
}

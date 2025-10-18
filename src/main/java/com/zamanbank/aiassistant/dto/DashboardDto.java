package com.zamanbank.aiassistant.dto;

import java.time.LocalDateTime;
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
public class DashboardDto {
    private BigDecimal totalBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal monthlySavings;
    private BigDecimal savingsRate;
    private Integer activeGoals;
    private Integer completedGoals;
    private BigDecimal totalGoalAmount;
    private BigDecimal totalSavedAmount;
    private List<RecentTransaction> recentTransactions;
    private List<GoalSummary> goalSummaries;
    private Map<String, BigDecimal> expensesByCategory;
    private List<MonthlyTrend> monthlyTrends;
    private String financialHealth;
    private List<String> recommendations;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTransaction {
        private Long id;
        private String description;
        private BigDecimal amount;
        private String category;
        private LocalDateTime date;
        private String type;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalSummary {
        private Long id;
        private String title;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private Double progressPercentage;
        private LocalDate targetDate;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTrend {
        private String month;
        private BigDecimal income;
        private BigDecimal expenses;
        private BigDecimal savings;
    }
}

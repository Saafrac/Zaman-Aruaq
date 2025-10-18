package com.zamanbank.aiassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialAnalysis {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal savingsRate;
    private Map<String, BigDecimal> expensesByCategory;
    private String aiRecommendations;
    private Double riskScore;
    private String spendingPattern;
    private BigDecimal monthlySurplus;
}


package com.zamanbank.aiassistant.dto;

import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private UserStatus status;
    
    // Финансовые данные
    private Double monthlyIncome;
    private Double monthlyExpenses;
    private Double currentSavings;
    
    // Временные метки
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Вычисляемые поля
    private BigDecimal netWorth;
    private BigDecimal monthlySavings;
    
    // Методы для вычисления
    public Double getNetWorth() {
        if (currentSavings != null) {
            return currentSavings;
        }
        return Double.valueOf(0);
    }

    public Double getMonthlySavings() {
        if (monthlyIncome != null && monthlyExpenses != null) {
            return monthlyIncome - monthlyExpenses;
        }
        return 0.0;
    }
}

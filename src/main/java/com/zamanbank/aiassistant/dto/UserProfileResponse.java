package com.zamanbank.aiassistant.dto;

import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import java.util.UUID;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    
    // Основная информация
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
    
    // Финансовая информация
    private Double monthlyIncome;
    private Double monthlyExpenses;
    private Double currentSavings;
    private Double netWorth;
    private Double monthlySavings;
    
    // Предпочтения и настройки (будут добавлены позже)
    
    // Временные метки
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    
    // Статистика
    private Integer totalGoals;
    private Integer activeGoals;
    private Integer completedGoals;
    private Integer totalTransactions;
    private Integer totalConversations;
    
    // Вычисляемые поля
    public Double getNetWorth() {
        return currentSavings != null ? currentSavings : 0.0;
    }
    
    public Double getMonthlySavings() {
        if (monthlyIncome != null && monthlyExpenses != null) {
            return monthlyIncome - monthlyExpenses;
        }
        return 0.0;
    }
    
    // Методы для получения полного имени
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Метод для получения инициалов
    public String getInitials() {
        if (firstName != null && lastName != null) {
            return (firstName.charAt(0) + "" + lastName.charAt(0)).toUpperCase();
        }
        return "U";
    }
}

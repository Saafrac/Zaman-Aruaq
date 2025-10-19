package com.zamanbank.aiassistant.dto;

import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserUpdateRequest {
    
    @Size(min = 2, max = 50, message = "Имя пользователя должно содержать от 2 до 50 символов")
    private String firstName;
    
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    private String lastName;
    
    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    private String email;
    
    @Size(min = 6, max = 100, message = "Пароль должен содержать от 6 до 100 символов")
    private String password;
    
    private UserRole role;
    private UserStatus status;
    
    // Финансовые данные
    @DecimalMin(value = "0.0", message = "Месячный доход не может быть отрицательным")
    private Double monthlyIncome;
    
    @DecimalMin(value = "0.0", message = "Месячные расходы не могут быть отрицательными")
    private Double monthlyExpenses;
    
    @DecimalMin(value = "0.0", message = "Текущие сбережения не могут быть отрицательными")
    private Double currentSavings;
}

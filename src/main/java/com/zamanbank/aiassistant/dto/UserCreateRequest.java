package com.zamanbank.aiassistant.dto;

import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCreateRequest {
    
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 2, max = 50, message = "Имя пользователя должно содержать от 2 до 50 символов")
    private String firstName;
    
    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 2, max = 50, message = "Фамилия должна содержать от 2 до 50 символов")
    private String lastName;
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    private String email;
    
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100, message = "Пароль должен содержать от 6 до 100 символов")
    private String password;
    
    private UserRole role = UserRole.CLIENT;
    private UserStatus status = UserStatus.ACTIVE;
    
    private Double monthlyIncome;
    private Double monthlyExpenses;
    private Double currentSavings;
}

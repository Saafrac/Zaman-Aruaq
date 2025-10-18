package com.zamanbank.aiassistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    
    @NotBlank(message = "Текущий пароль обязателен")
    private String currentPassword;
    
    @NotBlank(message = "Новый пароль обязателен")
    @Size(min = 6, max = 100, message = "Новый пароль должен содержать от 6 до 100 символов")
    private String newPassword;
    
    @NotBlank(message = "Подтверждение пароля обязательно")
    private String confirmPassword;
}

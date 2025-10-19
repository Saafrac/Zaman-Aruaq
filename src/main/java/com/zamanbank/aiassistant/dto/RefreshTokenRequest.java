package com.zamanbank.aiassistant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token обязателен")
    private String refreshToken;
}

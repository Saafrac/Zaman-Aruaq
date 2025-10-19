package com.zamanbank.aiassistant.dto;

import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private UserRole role;
        private UserStatus status;
        private LocalDateTime lastLogin;
    }
}

package com.zamanbank.aiassistant.mapper;

import com.zamanbank.aiassistant.dto.LoginResponse;
import com.zamanbank.aiassistant.dto.RegisterRequest;
import com.zamanbank.aiassistant.dto.UserCreateRequest;
import com.zamanbank.aiassistant.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthMapper {
    
    /**
     * Конвертирует RegisterRequest в UserCreateRequest
     */
    public UserCreateRequest toUserCreateRequest(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            return null;
        }
        
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setFirstName(registerRequest.getFirstName());
        userCreateRequest.setLastName(registerRequest.getLastName());
        userCreateRequest.setEmail(registerRequest.getEmail());
        userCreateRequest.setPassword(registerRequest.getPassword());
        userCreateRequest.setRole(registerRequest.getRole());
        userCreateRequest.setMonthlyIncome(registerRequest.getMonthlyIncome());
        userCreateRequest.setMonthlyExpenses(registerRequest.getMonthlyExpenses());
        userCreateRequest.setCurrentSavings(registerRequest.getCurrentSavings());
        
        return userCreateRequest;
    }
    
    /**
     * Создает LoginResponse из User и JWT токена
     */
    public LoginResponse toLoginResponse(User user, String token, Long expiresIn) {
        if (user == null) {
            return null;
        }
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpiresIn(expiresIn);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole());
        userInfo.setStatus(user.getStatus());
        userInfo.setLastLogin(LocalDateTime.now());
        
        response.setUser(userInfo);
        
        return response;
    }
    
    /**
     * Создает простой LoginResponse только с токеном
     */
    public LoginResponse toSimpleLoginResponse(String token, Long expiresIn) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpiresIn(expiresIn);
        
        return response;
    }
    
    /**
     * Создает UserInfo из User
     */
    public LoginResponse.UserInfo toUserInfo(User user) {
        if (user == null) {
            return null;
        }
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole());
        userInfo.setStatus(user.getStatus());
        userInfo.setLastLogin(LocalDateTime.now());
        
        return userInfo;
    }
}

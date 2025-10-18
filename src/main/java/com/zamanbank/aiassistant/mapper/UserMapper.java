package com.zamanbank.aiassistant.mapper;

import com.zamanbank.aiassistant.dto.UserCreateRequest;
import com.zamanbank.aiassistant.dto.UserProfileResponse;
import com.zamanbank.aiassistant.dto.UserResponse;
import com.zamanbank.aiassistant.dto.UserUpdateRequest;
import com.zamanbank.aiassistant.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public User toEntity(UserCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        // Устанавливаем значения по умолчанию
        user.setRole(com.zamanbank.aiassistant.model.enums.UserRole.CLIENT);
        user.setStatus(com.zamanbank.aiassistant.model.enums.UserStatus.ACTIVE);
        
        return user;
    }
    
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setMonthlyIncome(user.getMonthlyIncome());
        response.setMonthlyExpenses(user.getMonthlyExpenses());
        response.setCurrentSavings(user.getCurrentSavings());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        return response;
    }
    
    public void updateEntity(UserUpdateRequest request, User user) {
        if (request == null || user == null) {
            return;
        }
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getMonthlyIncome() != null) {
            user.setMonthlyIncome(request.getMonthlyIncome());
        }
        if (request.getMonthlyExpenses() != null) {
            user.setMonthlyExpenses(request.getMonthlyExpenses());
        }
        if (request.getCurrentSavings() != null) {
            user.setCurrentSavings(request.getCurrentSavings());
        }
    }
    
    public UserResponse toSafeResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setMonthlyIncome(user.getMonthlyIncome());
        response.setMonthlyExpenses(user.getMonthlyExpenses());
        response.setCurrentSavings(user.getCurrentSavings());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // Пароль не включается в ответ для безопасности
        
        return response;
    }
    
    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setMonthlyIncome(user.getMonthlyIncome());
        response.setMonthlyExpenses(user.getMonthlyExpenses());
        response.setCurrentSavings(user.getCurrentSavings());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // Статистика (можно добавить позже через отдельные запросы)
        response.setTotalGoals(0);
        response.setActiveGoals(0);
        response.setCompletedGoals(0);
        response.setTotalTransactions(0);
        response.setTotalConversations(0);
        
        return response;
    }
}

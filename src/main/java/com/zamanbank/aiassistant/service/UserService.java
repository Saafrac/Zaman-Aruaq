package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.dto.UserCreateRequest;
import com.zamanbank.aiassistant.dto.UserProfileResponse;
import com.zamanbank.aiassistant.dto.UserResponse;
import com.zamanbank.aiassistant.dto.UserUpdateRequest;
import com.zamanbank.aiassistant.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    
    // Получение текущего пользователя
    User getCurrentUser(Authentication authentication);
    UserResponse getCurrentUserInfo(Authentication authentication);
    UserProfileResponse getCurrentUserProfile(Authentication authentication);
    
    // CRUD операции
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    void deleteUser(Long id);
    
    // Внутренние методы для получения Entity
    User getUserEntityById(Long id);
    
    // Получение списка пользователей
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable);
    List<UserResponse> getUsersByRole(String role);
    List<UserResponse> getUsersByStatus(String status);
    
    // Поиск пользователей
    List<UserResponse> searchUsers(String query);
    
    // Обновление профиля текущего пользователя
    UserResponse updateCurrentUserProfile(Authentication authentication, UserUpdateRequest request);
    
    // Изменение пароля
    void changePassword(Authentication authentication, String oldPassword, String newPassword);
    
    // Активация/деактивация пользователя
    UserResponse activateUser(Long id);
    UserResponse deactivateUser(Long id);
    
    // Финансовые операции
    UserResponse updateFinancialInfo(Authentication authentication,
      Double monthlyIncome,
      Double monthlyExpenses,
      Double currentSavings);
}


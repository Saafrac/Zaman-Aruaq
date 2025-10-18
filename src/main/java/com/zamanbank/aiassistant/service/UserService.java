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
    
    User getCurrentUser(Authentication authentication);
    UserResponse getCurrentUserInfo(Authentication authentication);
    UserProfileResponse getCurrentUserProfile(Authentication authentication);
    
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    UserResponse getUserById(Long id);
    void deleteUser(Long id);
    
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable);
    List<UserResponse> getUsersByRole(String role);
    List<UserResponse> getUsersByStatus(String status);

    UserResponse updateCurrentUserProfile(Authentication authentication, UserUpdateRequest request);
    
    void changePassword(Authentication authentication, String oldPassword, String newPassword);
    
    UserResponse updateFinancialInfo(Authentication authentication,
      Double monthlyIncome,
      Double monthlyExpenses,
      Double currentSavings);
}


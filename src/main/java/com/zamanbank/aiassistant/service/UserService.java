package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.dto.UserCreateRequest;
import com.zamanbank.aiassistant.dto.UserProfileResponse;
import com.zamanbank.aiassistant.dto.UserResponse;
import com.zamanbank.aiassistant.dto.UserUpdateRequest;
import com.zamanbank.aiassistant.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
    
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(UUID id, UserUpdateRequest request);
    UserResponse getUserById(UUID id);
    void deleteUser(UUID id);
    
    List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(Pageable pageable);
    List<UserResponse> getUsersByRole(String role);
    List<UserResponse> getUsersByStatus(String status);
    
    // Дополнительные методы
    User getUserEntityById(UUID id);
    List<UserResponse> searchUsers(String query);
    UserResponse activateUser(UUID id);
    UserResponse deactivateUser(UUID id);
}


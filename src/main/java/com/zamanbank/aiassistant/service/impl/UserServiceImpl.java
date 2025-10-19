package com.zamanbank.aiassistant.service.impl;

import com.zamanbank.aiassistant.dto.UserCreateRequest;
import com.zamanbank.aiassistant.dto.UserProfileResponse;
import com.zamanbank.aiassistant.dto.UserResponse;
import com.zamanbank.aiassistant.dto.UserUpdateRequest;
import com.zamanbank.aiassistant.mapper.UserMapper;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import com.zamanbank.aiassistant.repository.UserRepository;
import com.zamanbank.aiassistant.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + email));
    }
    
    @Override
    public UserResponse getCurrentUserInfo(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return userMapper.toSafeResponse(user);
    }
    
    @Override
    public UserProfileResponse getCurrentUserProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        UserProfileResponse profile = userMapper.toProfileResponse(user);
        
        // Получаем статистику пользователя
        try {
            // Здесь можно добавить реальную статистику из базы данных
            // Пока что используем заглушки, но в будущем можно добавить:
            // - profile.setTotalGoals(goalRepository.countByUser(user));
            // - profile.setActiveGoals(goalRepository.countByUserAndStatus(user, GoalStatus.ACTIVE));
            // - profile.setTotalTransactions(transactionRepository.countByUser(user));
            // - profile.setTotalConversations(conversationRepository.countByUser(user));
            
            log.info("Получен полный профиль пользователя: {} с ID: {}", user.getEmail(), user.getId());
        } catch (Exception e) {
            log.warn("Не удалось получить статистику для пользователя {}: {}", user.getEmail(), e.getMessage());
        }
        
        return profile;
    }
    
    @Override
    public UserResponse createUser(UserCreateRequest request) {
        // Проверяем, что email не занят
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        log.info("Создан новый пользователь: {}", savedUser.getEmail());
        return userMapper.toSafeResponse(savedUser);
    }
    
    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        // Проверяем email на уникальность, если он изменился
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
        }
        
        userMapper.updateEntity(request, existingUser);
        
        // Обрабатываем пароль отдельно
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(existingUser);
        log.info("Обновлен пользователь: {}", updatedUser.getEmail());
        return userMapper.toSafeResponse(updatedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return userMapper.toSafeResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        userRepository.delete(user);
        log.info("Удален пользователь: {}", user.getEmail());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toSafeResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toSafeResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        return userRepository.findByRole(userRole).stream()
                .map(userMapper::toSafeResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByStatus(String status) {
        UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
        return userRepository.findByStatus(userStatus).stream()
                .map(userMapper::toSafeResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserResponse updateCurrentUserProfile(Authentication authentication, UserUpdateRequest request) {
        User currentUser = getCurrentUser(authentication);
        return updateUser(currentUser.getId(), request);
    }
    
    @Override
    public void changePassword(Authentication authentication, String oldPassword, String newPassword) {
        User currentUser = getCurrentUser(authentication);
        
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new RuntimeException("Неверный текущий пароль");
        }
        
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        currentUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(currentUser);
        log.info("Пароль изменен для пользователя: {}", currentUser.getEmail());
    }
    
    @Override
    public UserResponse updateFinancialInfo(Authentication authentication,
      Double monthlyIncome,
      Double monthlyExpenses,
      Double currentSavings
      ) {
        User currentUser = getCurrentUser(authentication);
        currentUser.setMonthlyIncome(monthlyIncome);
        currentUser.setMonthlyExpenses(monthlyExpenses);
        currentUser.setCurrentSavings(currentSavings);
        currentUser.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(currentUser);
        log.info("Финансовая информация обновлена для пользователя: {}", updatedUser.getEmail());
        return userMapper.toSafeResponse(updatedUser);
    }
}


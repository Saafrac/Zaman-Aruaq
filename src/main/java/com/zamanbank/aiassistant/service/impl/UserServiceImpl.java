package com.zamanbank.aiassistant.service.impl;

import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.repository.UserRepository;
import com.zamanbank.aiassistant.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public User getCurrentUser(Authentication authentication) {
        String phoneNumber = authentication.getName();
        return userRepository.findActiveUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + phoneNumber));
    }
    
    @Override
    public User createUser(User user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Пользователь с таким номером телефона уже существует");
        }
        
        User savedUser = userRepository.save(user);
        log.info("Создан новый пользователь: {}", savedUser.getPhoneNumber());
        return savedUser;
    }
    
    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        // Обновляем только разрешенные поля
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setMonthlyIncome(user.getMonthlyIncome());
        existingUser.setMonthlyExpenses(user.getMonthlyExpenses());
        existingUser.setCurrentSavings(user.getCurrentSavings());
        existingUser.setFinancialGoals(user.getFinancialGoals());
        existingUser.setSpendingHabits(user.getSpendingHabits());
        existingUser.setRiskProfile(user.getRiskProfile());
        
        User updatedUser = userRepository.save(existingUser);
        log.info("Обновлен пользователь: {}", updatedUser.getPhoneNumber());
        return updatedUser;
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}

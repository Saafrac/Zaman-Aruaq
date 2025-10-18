package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.model.User;
import org.springframework.security.core.Authentication;

public interface UserService {
    User getCurrentUser(Authentication authentication);
    User createUser(User user);
    User updateUser(User user);
    User getUserById(Long id);
    User getUserByPhoneNumber(String phoneNumber);
}


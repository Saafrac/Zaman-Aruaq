package com.zamanbank.aiassistant.security;

import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findActiveUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + phoneNumber));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPhoneNumber())
                .password("") // Пароль не используется в JWT аутентификации
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}


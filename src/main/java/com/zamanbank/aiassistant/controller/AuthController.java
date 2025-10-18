package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.security.JwtTokenProvider;
import com.zamanbank.aiassistant.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getPhoneNumber(),
                            request.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            
            User user = userService.getCurrentUser(authentication);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("user", Map.of(
                    "id", user.getId(),
                    "phoneNumber", user.getPhoneNumber(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "role", user.getRole()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при входе", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Неверные учетные данные"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = User.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .monthlyIncome(request.getMonthlyIncome())
                    .monthlyExpenses(request.getMonthlyExpenses())
                    .currentSavings(request.getCurrentSavings())
                    .build();
            
            User savedUser = userService.createUser(user);
            
            // Автоматический вход после регистрации
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    savedUser.getPhoneNumber(), null, null);
            String jwt = tokenProvider.generateTokenFromUsername(savedUser.getPhoneNumber());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("user", Map.of(
                    "id", savedUser.getId(),
                    "phoneNumber", savedUser.getPhoneNumber(),
                    "firstName", savedUser.getFirstName(),
                    "lastName", savedUser.getLastName(),
                    "role", savedUser.getRole()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при регистрации", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(Authentication authentication) {
        try {
            String jwt = tokenProvider.generateToken(authentication);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при обновлении токена", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Не удалось обновить токен"));
        }
    }
    
    // DTO классы
    public static class LoginRequest {
        private String phoneNumber;
        private String password;
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class RegisterRequest {
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private String email;
        private Double monthlyIncome;
        private Double monthlyExpenses;
        private Double currentSavings;
        
        // Getters and setters
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Double getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
        public Double getMonthlyExpenses() { return monthlyExpenses; }
        public void setMonthlyExpenses(Double monthlyExpenses) { this.monthlyExpenses = monthlyExpenses; }
        public Double getCurrentSavings() { return currentSavings; }
        public void setCurrentSavings(Double currentSavings) { this.currentSavings = currentSavings; }
    }
}

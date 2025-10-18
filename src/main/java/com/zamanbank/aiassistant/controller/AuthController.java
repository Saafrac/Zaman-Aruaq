package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.*;
import com.zamanbank.aiassistant.mapper.AuthMapper;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.service.JwtService;
import com.zamanbank.aiassistant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API для аутентификации и авторизации")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    @Operation(summary = "Вход в систему")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = userService.getCurrentUser(authentication);
            String token = jwtService.generateToken(user);
            Long expiresIn = jwtService.getExpirationTime();
            
            LoginResponse response = authMapper.toLoginResponse(user, token, expiresIn);
            
            log.info("Пользователь {} успешно вошел в систему", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.warn("Неверные учетные данные для email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Неверные учетные данные"));
        } catch (Exception e) {
            log.error("Ошибка при входе", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Внутренняя ошибка сервера"));
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Конвертируем RegisterRequest в UserCreateRequest
            UserCreateRequest userCreateRequest = authMapper.toUserCreateRequest(request);
            
            // Создаем пользователя через UserService
            var userResponse = userService.createUser(userCreateRequest);
            
            // Получаем созданного пользователя для генерации токена
            User user = userService.getUserEntityById(userResponse.getId());
            String token = jwtService.generateToken(user);
            Long expiresIn = jwtService.getExpirationTime();
            
            LoginResponse response = authMapper.toLoginResponse(user, token, expiresIn);
            
            log.info("Пользователь {} успешно зарегистрирован", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Ошибка при регистрации", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Ошибка регистрации: " + e.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Обновление токена")
    public ResponseEntity<LoginResponse> refreshToken(Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            String token = jwtService.generateToken(user);
            Long expiresIn = jwtService.getExpirationTime();
            
            LoginResponse response = authMapper.toLoginResponse(user, token, expiresIn);
            
            log.info("Токен обновлен для пользователя {}", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при обновлении токена", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Не удалось обновить токен"));
        }
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Изменение пароля")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        try {
            // Проверяем совпадение паролей
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Пароли не совпадают"));
            }
            
            userService.changePassword(authentication, request.getCurrentPassword(), request.getNewPassword());
            
            log.info("Пароль изменен для пользователя {}", authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Пароль успешно изменен"));
            
        } catch (Exception e) {
            log.error("Ошибка при изменении пароля", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Выход из системы")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        try {
            // В простой реализации JWT токены не отзываются
            // В production можно добавить blacklist токенов
            SecurityContextHolder.clearContext();
            
            log.info("Пользователь {} вышел из системы", authentication.getName());
            return ResponseEntity.ok(Map.of("message", "Успешный выход из системы"));
            
        } catch (Exception e) {
            log.error("Ошибка при выходе", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при выходе из системы"));
        }
    }
    
    /**
     * Создает ответ с ошибкой
     */
    private LoginResponse createErrorResponse(String message) {
        LoginResponse response = new LoginResponse();
        response.setToken(null);
        response.setExpiresIn(0L);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setEmail("error");
        response.setUser(userInfo);
        
        return response;
    }
}


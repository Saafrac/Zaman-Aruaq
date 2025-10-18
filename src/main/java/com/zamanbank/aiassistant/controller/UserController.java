package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.UserCreateRequest;
import com.zamanbank.aiassistant.dto.UserProfileResponse;
import com.zamanbank.aiassistant.dto.UserResponse;
import com.zamanbank.aiassistant.dto.UserUpdateRequest;
import com.zamanbank.aiassistant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/test/endpoint")
    @Operation(summary = "Тестовый endpoint для проверки работы API")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Тестовый endpoint работает! Время: " + java.time.LocalDateTime.now());
    }
    
    @GetMapping("/test")
    @Operation(summary = "Тестовый эндпоинт — возвращает число 10 000")
    public ResponseEntity<BigDecimal> getTestAmount() {
        return ResponseEntity.ok(BigDecimal.valueOf(10000));
    }
    
    // Создание нового пользователя
    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Получение пользователя по ID
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    // Обновление пользователя
    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            UserResponse user = userService.updateUser(id, request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Удаление пользователя
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Получение всех пользователей
    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Получение пользователей с пагинацией
    @GetMapping("/page")
    @Operation(summary = "Получить пользователей с пагинацией")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        try {
            Page<UserResponse> users = userService.getAllUsers(pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Поиск пользователей
    @GetMapping("/search")
    @Operation(summary = "Поиск пользователей")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @Parameter(description = "Поисковый запрос") @RequestParam String query) {
        try {
            List<UserResponse> users = userService.searchUsers(query);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователей", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Получение пользователей по роли
    @GetMapping("/role/{role}")
    @Operation(summary = "Получить пользователей по роли")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        try {
            List<UserResponse> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Ошибка при получении пользователей по роли", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Получение пользователей по статусу
    @GetMapping("/status/{status}")
    @Operation(summary = "Получить пользователей по статусу")
    public ResponseEntity<List<UserResponse>> getUsersByStatus(@PathVariable String status) {
        try {
            List<UserResponse> users = userService.getUsersByStatus(status);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Ошибка при получении пользователей по статусу", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Активация пользователя
    @PostMapping("/{id}/activate")
    @Operation(summary = "Активировать пользователя")
    public ResponseEntity<UserResponse> activateUser(@PathVariable UUID id) {
        try {
            UserResponse user = userService.activateUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при активации пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Деактивация пользователя
    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Деактивировать пользователя")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable UUID id) {
        try {
            UserResponse user = userService.deactivateUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при деактивации пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Getter @Setter
    public static class FinancialInfoRequest {
        private Double monthlyIncome;
        private Double monthlyExpenses;
        private Double currentSavings;
    }
}
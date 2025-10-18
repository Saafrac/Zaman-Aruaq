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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {
    
    private final UserService userService;
    
    // Получение информации о текущем пользователе
    @GetMapping("/me")
    @Operation(summary = "Получить информацию о текущем пользователе")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        try {
            UserResponse user = userService.getCurrentUserInfo(authentication);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при получении информации о пользователе", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Получение полного профиля текущего пользователя
    @GetMapping("/me/profile")
    @Operation(summary = "Получить полный профиль текущего пользователя")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
        try {
            UserProfileResponse profile = userService.getCurrentUserProfile(authentication);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Ошибка при получении профиля пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Создание нового пользователя
    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN') or @userService.getCurrentUser(#authentication).id == #id")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            Authentication authentication) {
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
    @PreAuthorize("hasRole('ADMIN') or @userService.getCurrentUser(#authentication).id == #id")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByStatus(@PathVariable String status) {
        try {
            List<UserResponse> users = userService.getUsersByStatus(status);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Ошибка при получении пользователей по статусу", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Обновление профиля текущего пользователя
    @PutMapping("/me")
    @Operation(summary = "Обновить профиль текущего пользователя")
    public ResponseEntity<UserResponse> updateCurrentUserProfile(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {
        try {
            UserResponse user = userService.updateCurrentUserProfile(authentication, request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при обновлении профиля", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Изменение пароля
    @PostMapping("/me/change-password")
    @Operation(summary = "Изменить пароль")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        try {
            userService.changePassword(authentication, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при изменении пароля", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // Активация пользователя
    @PostMapping("/{id}/activate")
    @Operation(summary = "Активировать пользователя")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        try {
            UserResponse user = userService.deactivateUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при деактивации пользователя", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Обновление финансовой информации
    @PutMapping("/me/financial")
    @Operation(summary = "Обновить финансовую информацию")
    public ResponseEntity<UserResponse> updateFinancialInfo(
            @RequestBody FinancialInfoRequest request,
            Authentication authentication) {
        try {
            UserResponse user = userService.updateFinancialInfo(
                    authentication,
                    request.getMonthlyIncome(),
                    request.getMonthlyExpenses(),
                    request.getCurrentSavings()
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Ошибка при обновлении финансовой информации", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @Getter @Setter
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        
    }

    @Getter @Setter
    public static class FinancialInfoRequest {
        private Double monthlyIncome;
        private Double monthlyExpenses;
        private Double currentSavings;
        
    }
}

package com.zamanbank.aiassistant.repository;

import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Поиск по email
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Поиск по номеру телефона (для обратной совместимости)
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    
    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    // Поиск по роли
    List<User> findByRole(UserRole role);
    
    // Поиск по статусу
    List<User> findByStatus(UserStatus status);
    
    // Поиск по имени, фамилии или email
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email);
    
    // Поиск активных пользователей
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();
    
    // Поиск пользователей по роли и статусу
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
}


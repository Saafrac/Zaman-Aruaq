package com.zamanbank.aiassistant.repository;

import com.zamanbank.aiassistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}

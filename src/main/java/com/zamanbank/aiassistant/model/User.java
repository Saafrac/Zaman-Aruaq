package com.zamanbank.aiassistant.model;

import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String phoneNumber;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.CLIENT;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    // Финансовые данные
    private Double monthlyIncome;
    private Double monthlyExpenses;
    private Double currentSavings;
    
    // Предпочтения для AI
    @Column(columnDefinition = "TEXT")
    private String financialGoals;
    
    @Column(columnDefinition = "TEXT")
    private String spendingHabits;
    
    @Column(columnDefinition = "TEXT")
    private String riskProfile;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Связи
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FinancialGoal> goals;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Conversation> conversations;
}

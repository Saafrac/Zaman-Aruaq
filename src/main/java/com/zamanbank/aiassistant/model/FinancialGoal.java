package com.zamanbank.aiassistant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FinancialGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private GoalType type;
    
    @Enumerated(EnumType.STRING)
    private GoalPriority priority;
    
    @Enumerated(EnumType.STRING)
    private GoalStatus status;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private LocalDate targetDate;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal monthlyContribution;
    
    // AI рекомендации
    @Column(columnDefinition = "TEXT")
    private String aiRecommendations;
    
    @Column(columnDefinition = "TEXT")
    private String suggestedProducts;
    
    @Column(columnDefinition = "TEXT")
    private String motivationTips;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Прогресс в процентах
    public Double getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return currentAmount.divide(targetAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
    
    // Осталось дней до цели
    public Long getDaysRemaining() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }
}

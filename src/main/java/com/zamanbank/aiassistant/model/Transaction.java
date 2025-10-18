package com.zamanbank.aiassistant.model;

import com.zamanbank.aiassistant.model.enums.TransactionCategory;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Enumerated(EnumType.STRING)
    private TransactionCategory category;
    
    @Column(nullable = false)
    private String description;
    
    @Column
    private String merchant;
    
    @Column
    private String location;
    
    @Column
    private String accountNumber;
    
    @Column
    private String referenceNumber;
    
    // AI анализ
    @Column(columnDefinition = "TEXT")
    private String aiAnalysis;
    
    @Column
    private Boolean isEssential;
    
    @Column
    private Boolean isRecurring;
    
    @Column
    private String spendingPattern;
    
    @CreatedDate
    private LocalDateTime transactionDate;
    
    @CreatedDate
    private LocalDateTime createdAt;
}

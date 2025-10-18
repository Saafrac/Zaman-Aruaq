package com.zamanbank.aiassistant.model;

import com.zamanbank.aiassistant.model.enums.ConversationStatus;
import com.zamanbank.aiassistant.model.enums.ConversationType;
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
@Table(name = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    private ConversationType type;
    
    @Enumerated(EnumType.STRING)
    private ConversationStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String context;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column
    private String sessionId;
    
    @CreatedDate
    private LocalDateTime startedAt;
    
    @LastModifiedDate
    private LocalDateTime lastActivityAt;
    
    // Связи
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
}

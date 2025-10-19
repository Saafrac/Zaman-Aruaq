package com.zamanbank.aiassistant.repository;

import com.zamanbank.aiassistant.model.Conversation;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.ConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    List<Conversation> findByUserOrderByLastActivityAtDesc(User user);
    
    List<Conversation> findByUserAndStatusOrderByLastActivityAtDesc(User user, ConversationStatus status);
    
    @Query("SELECT c FROM Conversation c WHERE c.user = :user AND c.status = 'ACTIVE' ORDER BY c.lastActivityAt DESC")
    List<Conversation> findActiveConversationsByUser(@Param("user") User user);
    
    Optional<Conversation> findByUserAndSessionId(User user, String sessionId);
    
    @Query("SELECT c FROM Conversation c WHERE c.user = :user AND c.status = 'ACTIVE' ORDER BY c.lastActivityAt DESC LIMIT 1")
    Optional<Conversation> findLatestActiveConversationByUser(@Param("user") User user);
}


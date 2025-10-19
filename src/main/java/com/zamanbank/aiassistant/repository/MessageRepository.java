package com.zamanbank.aiassistant.repository;

import com.zamanbank.aiassistant.model.Conversation;
import com.zamanbank.aiassistant.model.Message;
import com.zamanbank.aiassistant.model.enums.MessageRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByConversationOrderByTimestampAsc(Conversation conversation);
    
    List<Message> findByConversationAndRoleOrderByTimestampAsc(Conversation conversation, MessageRole role);
    
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.timestamp DESC LIMIT :limit")
    List<Message> findLatestMessagesByConversation(@Param("conversation") Conversation conversation, 
                                                  @Param("limit") int limit);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.role = 'USER'")
    Long countUserMessagesByConversation(@Param("conversation") Conversation conversation);
}


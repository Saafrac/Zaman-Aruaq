package com.zamanbank.aiassistant.service.impl;

import com.zamanbank.aiassistant.model.Conversation;
import com.zamanbank.aiassistant.model.Message;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.ConversationStatus;
import com.zamanbank.aiassistant.model.enums.ConversationType;
import com.zamanbank.aiassistant.repository.ConversationRepository;
import com.zamanbank.aiassistant.repository.MessageRepository;
import com.zamanbank.aiassistant.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConversationServiceImpl implements ConversationService {
    
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    
    @Override
    public Conversation getOrCreateActiveConversation(User user) {
        return conversationRepository.findLatestActiveConversationByUser(user)
                .orElseGet(() -> createConversation(user, UUID.randomUUID().toString()));
    }
    
    @Override
    public Conversation createConversation(User user, String sessionId) {
        Conversation conversation = Conversation.builder()
                .user(user)
                .type(ConversationType.TEXT)
                .status(ConversationStatus.ACTIVE)
                .sessionId(sessionId)
                .startedAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();
        
        Conversation savedConversation = conversationRepository.save(conversation);
        log.info("Создан новый разговор для пользователя: {}", user.getPhoneNumber());
        return savedConversation;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByUserOrderByLastActivityAtDesc(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Message> getConversationMessages(Long conversationId, User user) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Разговор не найден"));
        
        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        return messageRepository.findByConversationOrderByTimestampAsc(conversation);
    }
    
    @Override
    public Message saveMessage(Message message) {
        Message savedMessage = messageRepository.save(message);
        
        // Обновляем время последней активности разговора
        Conversation conversation = message.getConversation();
        conversation.setLastActivityAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        log.debug("Сохранено сообщение в разговоре: {}", conversation.getId());
        return savedMessage;
    }
    
    @Override
    public void closeConversation(Long conversationId, User user) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Разговор не найден"));
        
        if (!conversation.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        conversation.setStatus(ConversationStatus.COMPLETED);
        conversationRepository.save(conversation);
        log.info("Разговор закрыт: {}", conversationId);
    }
}

package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.model.Conversation;
import com.zamanbank.aiassistant.model.Message;
import com.zamanbank.aiassistant.model.User;

import java.util.List;

public interface ConversationService {
    Conversation getOrCreateActiveConversation(User user);
    Conversation createConversation(User user, String sessionId);
    List<Conversation> getUserConversations(User user);
    List<Message> getConversationMessages(Long conversationId, User user);
    Message saveMessage(Message message);
    void closeConversation(Long conversationId, User user);
}


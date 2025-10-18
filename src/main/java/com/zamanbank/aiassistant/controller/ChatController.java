package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.AiResponse;
import com.zamanbank.aiassistant.model.Conversation;
import com.zamanbank.aiassistant.model.Message;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.MessageRole;
import com.zamanbank.aiassistant.service.AiService;
import com.zamanbank.aiassistant.service.ConversationService;
import com.zamanbank.aiassistant.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private final AiService aiService;
    private final ConversationService conversationService;
    private final UserService userService;
    
    @PostMapping("/message")
    public ResponseEntity<AiResponse> sendMessage(
            @RequestBody ChatRequest request,
            Authentication authentication) {
        
        try {
            User user = userService.getCurrentUser(authentication);
            Conversation conversation = conversationService.getOrCreateActiveConversation(user);
            
            // Сохраняем сообщение пользователя
            Message userMessage = Message.builder()
                    .conversation(conversation)
                    .role(MessageRole.USER)
                    .content(request.getMessage())
                    .build();
            conversationService.saveMessage(userMessage);
            
            // Обрабатываем сообщение через AI
            AiResponse response = aiService.processMessage(
                    request.getMessage(), 
                    conversation, 
                    user
            );
            
            // Сохраняем ответ AI
            Message aiMessage = Message.builder()
                    .conversation(conversation)
                    .role(MessageRole.ASSISTANT)
                    .content(response.getContent())
                    .intent(response.getIntent())
                    .sentiment(response.getSentiment())
                    .confidence(response.getConfidence())
                    .suggestedActions(response.getSuggestedActions())
                    .build();
            conversationService.saveMessage(aiMessage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения", e);
            return ResponseEntity.internalServerError()
                    .body(AiResponse.builder()
                            .content("Произошла ошибка. Попробуйте еще раз.")
                            .build());
        }
    }
    
    @PostMapping("/voice")
    public ResponseEntity<AiResponse> sendVoiceMessage(
            @RequestParam("audio") MultipartFile audioFile,
            Authentication authentication) {
        
        try {
            User user = userService.getCurrentUser(authentication);
            Conversation conversation = conversationService.getOrCreateActiveConversation(user);
            
            // Конвертируем голос в текст
            String transcribedText = aiService.speechToText(audioFile.getBytes());
            
            if (transcribedText.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(AiResponse.builder()
                                .content("Не удалось распознать речь. Попробуйте еще раз.")
                                .build());
            }
            
            // Обрабатываем как обычное текстовое сообщение
            AiResponse response = aiService.processMessage(transcribedText, conversation, user);
            
            // Сохраняем сообщения
            Message userMessage = Message.builder()
                    .conversation(conversation)
                    .role(MessageRole.USER)
                    .content(transcribedText)
                    .build();
            conversationService.saveMessage(userMessage);
            
            Message aiMessage = Message.builder()
                    .conversation(conversation)
                    .role(MessageRole.ASSISTANT)
                    .content(response.getContent())
                    .build();
            conversationService.saveMessage(aiMessage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при обработке голосового сообщения", e);
            return ResponseEntity.internalServerError()
                    .body(AiResponse.builder()
                            .content("Произошла ошибка при обработке голоса.")
                            .build());
        }
    }
    
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getConversations(Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            List<Conversation> conversations = conversationService.getUserConversations(user);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Ошибка при получении списка разговоров", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable Long conversationId,
            Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            List<Message> messages = conversationService.getConversationMessages(conversationId, user);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Ошибка при получении сообщений", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/conversations/{conversationId}/close")
    public ResponseEntity<Void> closeConversation(
            @PathVariable Long conversationId,
            Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            conversationService.closeConversation(conversationId, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при закрытии разговора", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // DTO для запроса
    public static class ChatRequest {
        private String message;
        private String sessionId;
        
        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
}

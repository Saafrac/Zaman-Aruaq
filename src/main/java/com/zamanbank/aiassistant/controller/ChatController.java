package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.model.Conversation;
import com.zamanbank.aiassistant.model.Message;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.service.ConversationService;
import com.zamanbank.aiassistant.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private final ConversationService conversationService;
    private final UserService userService;
    
    @PostMapping("/message")
    public ResponseEntity<SimpleResponse> sendMessage(
            @RequestBody ChatRequest request,
            Authentication authentication) {
        
        try {
            User user = userService.getCurrentUser(authentication);
            Conversation conversation = conversationService.getOrCreateActiveConversation(user);
            
            // Простая заглушка - возвращаем эхо сообщения
            SimpleResponse response = new SimpleResponse();
            response.setContent("AI сервис временно недоступен. Ваше сообщение: " + request.getMessage());
            response.setStatus("success");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения", e);
            SimpleResponse errorResponse = new SimpleResponse();
            errorResponse.setContent("Произошла ошибка. Попробуйте еще раз.");
            errorResponse.setStatus("error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @PostMapping("/voice")
    public ResponseEntity<SimpleResponse> sendVoiceMessage(
            @RequestParam("audio") MultipartFile audioFile,
            Authentication authentication) {
        
        try {
            User user = userService.getCurrentUser(authentication);
            Conversation conversation = conversationService.getOrCreateActiveConversation(user);
            
            // Простая заглушка для голосовых сообщений
            SimpleResponse response = new SimpleResponse();
            response.setContent("Голосовые сообщения временно недоступны. AI сервис находится в разработке.");
            response.setStatus("info");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Ошибка при обработке голосового сообщения", e);
            SimpleResponse errorResponse = new SimpleResponse();
            errorResponse.setContent("Произошла ошибка при обработке голоса.");
            errorResponse.setStatus("error");
            return ResponseEntity.internalServerError().body(errorResponse);
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
    @Getter @Setter
    public static class ChatRequest {
        private String message;
        private String sessionId;
    }
    
    // Простой DTO для ответа
    @Getter @Setter
    public static class SimpleResponse {
        private String content;
        private String status;
    }
}

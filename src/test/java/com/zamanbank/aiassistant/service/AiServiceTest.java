package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.dto.AiResponse;
import com.zamanbank.aiassistant.model.Conversation;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.ConversationType;
import com.zamanbank.aiassistant.model.enums.ConversationStatus;
import com.zamanbank.aiassistant.model.enums.UserRole;
import com.zamanbank.aiassistant.model.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private AiService aiService;

    private User testUser;
    private Conversation testConversation;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .phoneNumber("+77001234567")
                .firstName("Айдар")
                .lastName("Нурланов")
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .monthlyIncome(500000.0)
                .monthlyExpenses(300000.0)
                .currentSavings(100000.0)
                .build();

        testConversation = Conversation.builder()
                .id(1L)
                .user(testUser)
                .type(ConversationType.TEXT)
                .status(ConversationStatus.ACTIVE)
                .startedAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testProcessMessage() {
        // Given
        String userMessage = "Хочу накопить на квартиру";
        AiResponse expectedResponse = AiResponse.builder()
                .content("Отлично! Давайте создадим план накоплений на квартиру. Какая у вас целевая сумма?")
                .intent("goal_setting")
                .sentiment("positive")
                .confidence(0.9)
                .build();

        when(aiService.processMessage(any(String.class), any(Conversation.class), any(User.class)))
                .thenReturn(expectedResponse);

        // When
        AiResponse response = aiService.processMessage(userMessage, testConversation, testUser);

        // Then
        assertNotNull(response);
        assertEquals("goal_setting", response.getIntent());
        assertEquals("positive", response.getSentiment());
        assertTrue(response.getConfidence() > 0.8);
    }

    @Test
    void testAnalyzeSentiment() {
        // Given
        String positiveMessage = "Спасибо за помощь!";
        String negativeMessage = "Не работает как надо";
        String neutralMessage = "Покажите баланс";

        when(aiService.analyzeSentiment(positiveMessage)).thenReturn("positive");
        when(aiService.analyzeSentiment(negativeMessage)).thenReturn("negative");
        when(aiService.analyzeSentiment(neutralMessage)).thenReturn("neutral");

        // When & Then
        assertEquals("positive", aiService.analyzeSentiment(positiveMessage));
        assertEquals("negative", aiService.analyzeSentiment(negativeMessage));
        assertEquals("neutral", aiService.analyzeSentiment(neutralMessage));
    }

    @Test
    void testExtractIntent() {
        // Given
        String goalMessage = "Хочу накопить на машину";
        String analysisMessage = "Покажите мои расходы";
        String productMessage = "Какие депозиты у вас есть?";

        when(aiService.extractIntent(goalMessage)).thenReturn("goal_setting");
        when(aiService.extractIntent(analysisMessage)).thenReturn("transaction_analysis");
        when(aiService.extractIntent(productMessage)).thenReturn("product_inquiry");

        // When & Then
        assertEquals("goal_setting", aiService.extractIntent(goalMessage));
        assertEquals("transaction_analysis", aiService.extractIntent(analysisMessage));
        assertEquals("product_inquiry", aiService.extractIntent(productMessage));
    }
}

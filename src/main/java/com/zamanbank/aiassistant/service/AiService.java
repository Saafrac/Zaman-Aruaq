package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.FinancialGoal;
import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.Conversation;
import com.zamanbank.aiassistant.model.Message;
import com.zamanbank.aiassistant.dto.AiResponse;
import com.zamanbank.aiassistant.dto.FinancialAnalysis;
import com.zamanbank.aiassistant.dto.GoalRecommendation;
import com.zamanbank.aiassistant.dto.ProductRecommendation;

import java.util.List;
import java.util.Map;

public interface AiService {
    
    /**
     * Обработка сообщения пользователя и генерация ответа
     */
    AiResponse processMessage(String userMessage, Conversation conversation, User user);
    
    /**
     * Анализ финансового состояния пользователя
     */
    FinancialAnalysis analyzeFinancialState(User user, List<Transaction> transactions);
    
    /**
     * Генерация рекомендаций по финансовым целям
     */
    List<GoalRecommendation> generateGoalRecommendations(User user, List<FinancialGoal> goals);
    
    /**
     * Подбор банковских продуктов на основе профиля пользователя
     */
    List<ProductRecommendation> recommendProducts(User user, String goalType);
    
    /**
     * Анализ транзакций и выявление паттернов
     */
    Map<String, Object> analyzeTransactionPatterns(List<Transaction> transactions);
    
    /**
     * Генерация мотивационных сообщений
     */
    String generateMotivationMessage(User user, FinancialGoal goal);
    
    /**
     * Анализ стресса и рекомендации по альтернативным способам борьбы
     */
    String generateStressReliefRecommendations(User user, List<Transaction> recentTransactions);
    
    /**
     * Конвертация голоса в текст
     */
    String speechToText(byte[] audioData);
    
    /**
     * Конвертация текста в голос
     */
    byte[] textToSpeech(String text);
    
    /**
     * Анализ тональности сообщения
     */
    String analyzeSentiment(String message);
    
    /**
     * Извлечение намерений из сообщения
     */
    String extractIntent(String message);
}

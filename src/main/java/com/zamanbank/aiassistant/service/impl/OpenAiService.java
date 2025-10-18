package com.zamanbank.aiassistant.service.impl;

import com.zamanbank.aiassistant.service.AiService;
import com.zamanbank.aiassistant.model.*;
import com.zamanbank.aiassistant.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiAudioApi.TranscriptionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService implements AiService {
    
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final WebClient webClient;
    
    @Value("${ai.openai.api-key}")
    private String apiKey;
    
    @Value("${ai.openai.base-url}")
    private String baseUrl;
    
    @Override
    public AiResponse processMessage(String userMessage, Conversation conversation, User user) {
        try {
            // Создаем контекст для AI
            String context = buildUserContext(user, conversation);
            
            // Формируем промпт с контекстом
            String systemPrompt = buildSystemPrompt(user);
            String fullPrompt = systemPrompt + "\n\nКонтекст пользователя:\n" + context + 
                               "\n\nСообщение пользователя: " + userMessage;
            
            Prompt prompt = new Prompt(fullPrompt);
            ChatResponse response = chatClient.call(prompt);
            
            String aiResponse = response.getResult().getOutput().getContent();
            
            // Анализируем намерения и тональность
            String intent = extractIntent(userMessage);
            String sentiment = analyzeSentiment(userMessage);
            
            return AiResponse.builder()
                    .content(aiResponse)
                    .intent(intent)
                    .sentiment(sentiment)
                    .confidence(0.85) // Можно улучшить с помощью ML модели
                    .suggestedActions(generateSuggestedActions(intent, user))
                    .build();
                    
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения", e);
            return AiResponse.builder()
                    .content("Извините, произошла ошибка. Попробуйте еще раз.")
                    .intent("error")
                    .sentiment("neutral")
                    .confidence(0.0)
                    .build();
        }
    }
    
    @Override
    public FinancialAnalysis analyzeFinancialState(User user, List<Transaction> transactions) {
        try {
            // Анализируем доходы и расходы
            BigDecimal totalIncome = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            BigDecimal totalExpenses = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Анализируем категории расходов
            Map<TransactionCategory, BigDecimal> expensesByCategory = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .collect(Collectors.groupingBy(
                            Transaction::getCategory,
                            Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                    ));
            
            // Генерируем AI рекомендации
            String analysisPrompt = String.format(
                    "Проанализируйте финансовое состояние пользователя:\n" +
                    "Доходы: %s тенге\n" +
                    "Расходы: %s тенге\n" +
                    "Сбережения: %s тенге\n" +
                    "Расходы по категориям: %s\n\n" +
                    "Дайте рекомендации по оптимизации финансов.",
                    totalIncome, totalExpenses, user.getCurrentSavings(), expensesByCategory
            );
            
            Prompt prompt = new Prompt(analysisPrompt);
            ChatResponse response = chatClient.call(prompt);
            String aiRecommendations = response.getResult().getOutput().getContent();
            
            return FinancialAnalysis.builder()
                    .totalIncome(totalIncome)
                    .totalExpenses(totalExpenses)
                    .savingsRate(calculateSavingsRate(totalIncome, totalExpenses))
                    .expensesByCategory(expensesByCategory)
                    .aiRecommendations(aiRecommendations)
                    .riskScore(calculateRiskScore(user, transactions))
                    .build();
                    
        } catch (Exception e) {
            log.error("Ошибка при анализе финансового состояния", e);
            return FinancialAnalysis.builder().build();
        }
    }
    
    @Override
    public List<GoalRecommendation> generateGoalRecommendations(User user, List<FinancialGoal> goals) {
        try {
            String prompt = String.format(
                    "Пользователь имеет следующие финансовые цели: %s\n" +
                    "Текущие сбережения: %s тенге\n" +
                    "Месячный доход: %s тенге\n\n" +
                    "Предложите оптимизированный план достижения целей с учетом исламских финансовых принципов.",
                    goals.stream().map(FinancialGoal::getTitle).collect(Collectors.joining(", ")),
                    user.getCurrentSavings(),
                    user.getMonthlyIncome()
            );
            
            Prompt aiPrompt = new Prompt(prompt);
            ChatResponse response = chatClient.call(aiPrompt);
            String recommendations = response.getResult().getOutput().getContent();
            
            // Парсим рекомендации и создаем объекты
            return parseGoalRecommendations(recommendations, goals);
            
        } catch (Exception e) {
            log.error("Ошибка при генерации рекомендаций по целям", e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<ProductRecommendation> recommendProducts(User user, String goalType) {
        try {
            String prompt = String.format(
                    "Пользователь хочет достичь цели: %s\n" +
                    "Профиль риска: %s\n" +
                    "Предпочтения: %s\n\n" +
                    "Рекомендуйте подходящие исламские банковские продукты из Zaman Bank.",
                    goalType, user.getRiskProfile(), user.getFinancialGoals()
            );
            
            Prompt aiPrompt = new Prompt(prompt);
            ChatResponse response = chatClient.call(aiPrompt);
            String recommendations = response.getResult().getOutput().getContent();
            
            return parseProductRecommendations(recommendations);
            
        } catch (Exception e) {
            log.error("Ошибка при подборе продуктов", e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public Map<String, Object> analyzeTransactionPatterns(List<Transaction> transactions) {
        // Анализ паттернов трат
        Map<String, Object> patterns = new HashMap<>();
        
        // Группировка по дням недели
        Map<String, Long> byDayOfWeek = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getDayOfWeek().toString(),
                        Collectors.counting()
                ));
        
        // Группировка по времени дня
        Map<String, Long> byTimeOfDay = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> getTimeOfDay(t.getTransactionDate().getHour()),
                        Collectors.counting()
                ));
        
        patterns.put("byDayOfWeek", byDayOfWeek);
        patterns.put("byTimeOfDay", byTimeOfDay);
        patterns.put("totalTransactions", transactions.size());
        
        return patterns;
    }
    
    @Override
    public String generateMotivationMessage(User user, FinancialGoal goal) {
        try {
            String prompt = String.format(
                    "Создайте мотивационное сообщение для пользователя, который копит на: %s\n" +
                    "Текущий прогресс: %.1f%%\n" +
                    "Осталось дней: %d\n" +
                    "Стиль: дружелюбный, поддерживающий, в духе исламских ценностей",
                    goal.getTitle(),
                    goal.getProgressPercentage(),
                    goal.getDaysRemaining()
            );
            
            Prompt aiPrompt = new Prompt(prompt);
            ChatResponse response = chatClient.call(aiPrompt);
            return response.getResult().getOutput().getContent();
            
        } catch (Exception e) {
            log.error("Ошибка при генерации мотивационного сообщения", e);
            return "Продолжайте идти к своей цели! Каждый шаг приближает вас к мечте.";
        }
    }
    
    @Override
    public String generateStressReliefRecommendations(User user, List<Transaction> recentTransactions) {
        try {
            String prompt = "Пользователь испытывает стресс и склонен к импульсивным покупкам. " +
                           "Предложите альтернативные способы борьбы со стрессом, основанные на исламских принципах: " +
                           "молитва, медитация, физическая активность, общение с близкими, хобби.";
            
            Prompt aiPrompt = new Prompt(prompt);
            ChatResponse response = chatClient.call(aiPrompt);
            return response.getResult().getOutput().getContent();
            
        } catch (Exception e) {
            log.error("Ошибка при генерации рекомендаций по борьбе со стрессом", e);
            return "Попробуйте прогуляться, помолиться или позвонить близкому человеку.";
        }
    }
    
    @Override
    public String speechToText(byte[] audioData) {
        try {
            // Используем Whisper API для конвертации речи в текст
            return webClient.post()
                    .uri(baseUrl + "/v1/audio/transcriptions")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(createTranscriptionRequest(audioData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("Ошибка при конвертации речи в текст", e);
            return "";
        }
    }
    
    @Override
    public byte[] textToSpeech(String text) {
        try {
            // Используем TTS API для конвертации текста в речь
            return webClient.post()
                    .uri(baseUrl + "/v1/audio/speech")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(createSpeechRequest(text))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            log.error("Ошибка при конвертации текста в речь", e);
            return new byte[0];
        }
    }
    
    @Override
    public String analyzeSentiment(String message) {
        try {
            String prompt = "Определите тональность сообщения (positive, negative, neutral): " + message;
            Prompt aiPrompt = new Prompt(prompt);
            ChatResponse response = chatClient.call(aiPrompt);
            return response.getResult().getOutput().getContent().toLowerCase();
        } catch (Exception e) {
            log.error("Ошибка при анализе тональности", e);
            return "neutral";
        }
    }
    
    @Override
    public String extractIntent(String message) {
        try {
            String prompt = "Определите намерение пользователя из сообщения: " + message + 
                           "\nВозможные намерения: goal_setting, transaction_analysis, product_inquiry, stress_relief, general_question";
            Prompt aiPrompt = new Prompt(prompt);
            ChatResponse response = chatClient.call(aiPrompt);
            return response.getResult().getOutput().getContent().toLowerCase();
        } catch (Exception e) {
            log.error("Ошибка при извлечении намерений", e);
            return "general_question";
        }
    }
    
    // Вспомогательные методы
    private String buildUserContext(User user, Conversation conversation) {
        return String.format(
                "Пользователь: %s %s\n" +
                "Доход: %s тенге/месяц\n" +
                "Расходы: %s тенге/месяц\n" +
                "Сбережения: %s тенге\n" +
                "Цели: %s",
                user.getFirstName(), user.getLastName(),
                user.getMonthlyIncome(),
                user.getMonthlyExpenses(),
                user.getCurrentSavings(),
                user.getFinancialGoals()
        );
    }
    
    private String buildSystemPrompt(User user) {
        return "Вы - AI-ассистент Zaman Bank, специализирующийся на исламских финансах. " +
               "Ваша задача - помочь пользователю с финансовым планированием, " +
               "рекомендациями по продуктам и достижением финансовых целей. " +
               "Всегда учитывайте исламские принципы в рекомендациях.";
    }
    
    private String generateSuggestedActions(String intent, User user) {
        switch (intent) {
            case "goal_setting":
                return "Создать новую финансовую цель";
            case "transaction_analysis":
                return "Показать анализ расходов";
            case "product_inquiry":
                return "Показать подходящие продукты";
            default:
                return "Задать вопрос ассистенту";
        }
    }
    
    private BigDecimal calculateSavingsRate(BigDecimal income, BigDecimal expenses) {
        if (income.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return income.subtract(expenses).divide(income, 4, BigDecimal.ROUND_HALF_UP);
    }
    
    private Double calculateRiskScore(User user, List<Transaction> transactions) {
        // Простая логика расчета риска
        double baseScore = 0.5;
        if (user.getCurrentSavings() > user.getMonthlyIncome() * 6) baseScore -= 0.2;
        if (transactions.stream().anyMatch(t -> t.getCategory() == TransactionCategory.INVESTMENT_RETURN)) baseScore += 0.1;
        return Math.max(0.0, Math.min(1.0, baseScore));
    }
    
    private String getTimeOfDay(int hour) {
        if (hour < 6) return "night";
        if (hour < 12) return "morning";
        if (hour < 18) return "afternoon";
        return "evening";
    }
    
    private List<GoalRecommendation> parseGoalRecommendations(String recommendations, List<FinancialGoal> goals) {
        // Простая парсинг логика - в реальном проекте можно использовать JSON
        return goals.stream()
                .map(goal -> GoalRecommendation.builder()
                        .goalId(goal.getId())
                        .recommendation("Оптимизируйте план накоплений")
                        .priority("high")
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<ProductRecommendation> parseProductRecommendations(String recommendations) {
        // Простая парсинг логика
        return Arrays.asList(
                ProductRecommendation.builder()
                        .productName("Исламский депозит")
                        .description("Безрисковые накопления")
                        .suitability(0.9)
                        .build()
        );
    }
    
    private Object createTranscriptionRequest(byte[] audioData) {
        // Создание запроса для Whisper API
        return Map.of("file", audioData, "model", "whisper-1");
    }
    
    private Object createSpeechRequest(String text) {
        // Создание запроса для TTS API
        return Map.of(
                "model", "tts-1",
                "input", text,
                "voice", "alloy"
        );
    }
}

package com.zamanbank.aiassistant.service.impl;

import com.zamanbank.aiassistant.dto.DashboardDto;
import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.FinancialGoal;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import com.zamanbank.aiassistant.model.enums.GoalStatus;
import com.zamanbank.aiassistant.repository.TransactionRepository;
import com.zamanbank.aiassistant.repository.FinancialGoalRepository;
import com.zamanbank.aiassistant.repository.UserRepository;
import com.zamanbank.aiassistant.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    
    private final TransactionRepository transactionRepository;
    private final FinancialGoalRepository goalRepository;
    private final UserRepository userRepository;
    
    @Override
    public DashboardDto getDashboard(String userPhone) {
        log.info("Получение дашборда для пользователя: {}", userPhone);
        
        User user = userRepository.findByPhoneNumber(userPhone)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        // Получаем данные за последний месяц
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        
        List<Transaction> transactions = transactionRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
        List<FinancialGoal> goals = goalRepository.findByUser(user);
        
        // Рассчитываем показатели
        BigDecimal monthlyIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal monthlyExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal monthlySavings = monthlyIncome.subtract(monthlyExpenses);
        BigDecimal savingsRate = monthlyIncome.compareTo(BigDecimal.ZERO) > 0 ? 
            monthlySavings.divide(monthlyIncome, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) : 
            BigDecimal.ZERO;
        
        // Статистика по целям
        long activeGoals = goals.stream().filter(g -> g.getStatus() == GoalStatus.IN_PROGRESS).count();
        long completedGoals = goals.stream().filter(g -> g.getStatus() == GoalStatus.COMPLETED).count();
        
        BigDecimal totalGoalAmount = goals.stream()
                .map(FinancialGoal::getTargetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal totalSavedAmount = goals.stream()
                .map(FinancialGoal::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return DashboardDto.builder()
                .totalBalance(BigDecimal.valueOf(user.getCurrentSavings()))
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .monthlySavings(monthlySavings)
                .savingsRate(savingsRate)
                .activeGoals((int) activeGoals)
                .completedGoals((int) completedGoals)
                .totalGoalAmount(totalGoalAmount)
                .totalSavedAmount(totalSavedAmount)
                .recentTransactions(getRecentTransactions(transactions))
                .goalSummaries(getGoalSummaries(goals))
                .expensesByCategory(getExpensesByCategory(transactions))
                .monthlyTrends(getMonthlyTrends(user, 6))
                .financialHealth(calculateFinancialHealth(savingsRate))
                .recommendations(generateRecommendations(user, monthlySavings))
                .build();
    }
    
    @Override
    public DashboardDto getAnalytics(String userPhone, int months) {
        log.info("Получение аналитики для пользователя: {} за {} месяцев", userPhone, months);
        
        User user = userRepository.findByPhoneNumber(userPhone)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        LocalDate startDate = LocalDate.now().minusMonths(months);
        LocalDate endDate = LocalDate.now();
        
        List<Transaction> transactions = transactionRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
        List<FinancialGoal> goals = goalRepository.findByUser(user);
        
        // Аналогичные расчеты как в getDashboard, но за указанный период
        return getDashboard(userPhone); // Упрощенная реализация
    }
    
    @Override
    public DashboardDto getSummary(String userPhone) {
        log.info("Получение сводки для пользователя: {}", userPhone);
        
        User user = userRepository.findByPhoneNumber(userPhone)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        // Краткая сводка - основные показатели
        return getDashboard(userPhone); // Упрощенная реализация
    }
    
    private List<DashboardDto.RecentTransaction> getRecentTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .limit(5)
                .map(this::convertToRecentTransaction)
                .collect(Collectors.toList());
    }
    
    private List<DashboardDto.GoalSummary> getGoalSummaries(List<FinancialGoal> goals) {
        return goals.stream()
                .map(this::convertToGoalSummary)
                .collect(Collectors.toList());
    }
    
    private Map<String, BigDecimal> getExpensesByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }
    
    private List<DashboardDto.MonthlyTrend> getMonthlyTrends(User user, int months) {
        // Упрощенная реализация - можно расширить для получения данных по месяцам
        return List.of();
    }
    
    private String calculateFinancialHealth(BigDecimal savingsRate) {
        if (savingsRate.compareTo(new BigDecimal("20")) >= 0) {
            return "Отличное";
        } else if (savingsRate.compareTo(new BigDecimal("10")) >= 0) {
            return "Хорошее";
        } else if (savingsRate.compareTo(new BigDecimal("5")) >= 0) {
            return "Удовлетворительное";
        } else {
            return "Требует улучшения";
        }
    }
    
    private List<String> generateRecommendations(User user, BigDecimal monthlySavings) {
        List<String> recommendations = List.of();
        
        if (monthlySavings.compareTo(new BigDecimal("100000")) < 0) {
            recommendations = List.of(
                "Увеличьте ежемесячные сбережения",
                "Рассмотрите возможность дополнительного дохода",
                "Оптимизируйте расходы"
            );
        }
        
        return recommendations;
    }
    
    private DashboardDto.RecentTransaction convertToRecentTransaction(Transaction transaction) {
        return DashboardDto.RecentTransaction.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .category(transaction.getCategory().toString())
                .date(transaction.getTransactionDate())
                .type(transaction.getType().toString())
                .build();
    }
    
    private DashboardDto.GoalSummary convertToGoalSummary(FinancialGoal goal) {
        return DashboardDto.GoalSummary.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .progressPercentage(goal.getProgressPercentage())
                .targetDate(goal.getTargetDate())
                .status(goal.getStatus().toString())
                .build();
    }
}

package com.zamanbank.aiassistant.service.impl;

import com.zamanbank.aiassistant.dto.ReportDto;
import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.FinancialGoal;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import com.zamanbank.aiassistant.repository.TransactionRepository;
import com.zamanbank.aiassistant.repository.FinancialGoalRepository;
import com.zamanbank.aiassistant.repository.UserRepository;
import com.zamanbank.aiassistant.service.ReportService;
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
public class ReportServiceImpl implements ReportService {
    
    private final TransactionRepository transactionRepository;
    private final FinancialGoalRepository goalRepository;
    private final UserRepository userRepository;
    
    @Override
    public ReportDto generateReport(String userPhone, LocalDate startDate, LocalDate endDate, String reportType) {
        log.info("Генерация отчета для пользователя: {} за период с {} по {}", userPhone, startDate, endDate);
        
        User user = userRepository.findByPhoneNumber(userPhone)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        // Получаем транзакции за период
        List<Transaction> transactions = transactionRepository.findByUserAndTransactionDateBetween(user, startDate, endDate);
        
        // Рассчитываем доходы и расходы
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal netIncome = totalIncome.subtract(totalExpenses);
        BigDecimal savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0 ? 
            netIncome.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) : 
            BigDecimal.ZERO;
        
        // Расходы по категориям
        Map<String, BigDecimal> expensesByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().toString(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
        
        // Топ транзакции
        List<ReportDto.TransactionSummary> topTransactions = transactions.stream()
                .sorted((t1, t2) -> t2.getAmount().compareTo(t1.getAmount()))
                .limit(5)
                .map(this::convertToTransactionSummary)
                .collect(Collectors.toList());
        
        // Прогресс по целям
        List<FinancialGoal> goals = goalRepository.findByUser(user);
        List<ReportDto.GoalProgress> goalProgress = goals.stream()
                .map(this::convertToGoalProgress)
                .collect(Collectors.toList());
        
        String summary = generateSummary(reportType, totalIncome, totalExpenses, netIncome);
        
        return ReportDto.builder()
                .reportType(reportType)
                .startDate(startDate)
                .endDate(endDate)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netIncome(netIncome)
                .savingsRate(savingsRate)
                .expensesByCategory(expensesByCategory)
                .topTransactions(topTransactions)
                .goalProgress(goalProgress)
                .summary(summary)
                .generatedAt(LocalDate.now())
                .build();
    }
    
    private ReportDto.TransactionSummary convertToTransactionSummary(Transaction transaction) {
        return ReportDto.TransactionSummary.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .category(transaction.getCategory().toString())
                .date(transaction.getTransactionDate().toLocalDate())
                .build();
    }
    
    private ReportDto.GoalProgress convertToGoalProgress(FinancialGoal goal) {
        return ReportDto.GoalProgress.builder()
                .goalId(goal.getId())
                .title(goal.getTitle())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .progressPercentage(goal.getProgressPercentage())
                .targetDate(goal.getTargetDate())
                .build();
    }
    
    private String generateSummary(String reportType, BigDecimal income, BigDecimal expenses, BigDecimal netIncome) {
        String period = getPeriodName(reportType);
        return String.format(
                "За %s период ваш доход составил %s тенге, расходы - %s тенге. " +
                "Чистый доход: %s тенге. Рекомендуется увеличить сбережения на %s тенге в месяц для достижения финансовых целей.",
                period, income, expenses, netIncome, netIncome.divide(new BigDecimal("12"), 0, BigDecimal.ROUND_HALF_UP)
        );
    }
    
    private String getPeriodName(String reportType) {
        switch (reportType) {
            case "MONTHLY": return "месячный";
            case "QUARTERLY": return "квартальный";
            case "HALF_YEARLY": return "полугодовой";
            case "YEARLY": return "годовой";
            default: return "выбранный";
        }
    }
}

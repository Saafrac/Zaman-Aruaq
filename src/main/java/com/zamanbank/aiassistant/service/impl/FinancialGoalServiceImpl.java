package com.zamanbank.aiassistant.service.impl;

import com.zamanbank.aiassistant.model.FinancialGoal;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.GoalStatus;
import com.zamanbank.aiassistant.repository.FinancialGoalRepository;
import com.zamanbank.aiassistant.service.FinancialGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FinancialGoalServiceImpl implements FinancialGoalService {
    
    private final FinancialGoalRepository goalRepository;
    
    @Override
    public FinancialGoal createGoal(FinancialGoal goal) {
        // Валидация данных
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Целевая сумма должна быть больше нуля");
        }
        
        if (goal.getTargetDate().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("Целевая дата не может быть в прошлом");
        }
        
        FinancialGoal savedGoal = goalRepository.save(goal);
        log.info("Создана новая финансовая цель: {} для пользователя: {}", 
                savedGoal.getTitle(), savedGoal.getUser().getPhoneNumber());
        return savedGoal;
    }
    
    @Override
    public FinancialGoal updateGoal(FinancialGoal goal) {
        FinancialGoal existingGoal = goalRepository.findById(goal.getId())
                .orElseThrow(() -> new RuntimeException("Цель не найдена"));
        
        if (!existingGoal.getUser().getId().equals(goal.getUser().getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        // Обновляем поля
        existingGoal.setTitle(goal.getTitle());
        existingGoal.setDescription(goal.getDescription());
        existingGoal.setTargetAmount(goal.getTargetAmount());
        existingGoal.setTargetDate(goal.getTargetDate());
        existingGoal.setMonthlyContribution(goal.getMonthlyContribution());
        existingGoal.setStatus(goal.getStatus());
        existingGoal.setAiRecommendations(goal.getAiRecommendations());
        existingGoal.setSuggestedProducts(goal.getSuggestedProducts());
        existingGoal.setMotivationTips(goal.getMotivationTips());
        
        FinancialGoal updatedGoal = goalRepository.save(existingGoal);
        log.info("Обновлена финансовая цель: {}", updatedGoal.getId());
        return updatedGoal;
    }
    
    @Override
    @Transactional(readOnly = true)
    public FinancialGoal getGoalById(Long goalId, User user) {
        FinancialGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Цель не найдена"));
        
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        return goal;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FinancialGoal> getUserGoals(User user) {
        return goalRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FinancialGoal> getGoalsByStatus(GoalStatus status, User user) {
        return goalRepository.findByUserAndStatus(user, status);
    }
    
    @Override
    public FinancialGoal contributeToGoal(Long goalId, BigDecimal amount, User user) {
        FinancialGoal goal = getGoalById(goalId, user);
        
        if (goal.getStatus() != GoalStatus.IN_PROGRESS) {
            throw new RuntimeException("Нельзя вносить средства в неактивную цель");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Сумма должна быть больше нуля");
        }
        
        // Обновляем текущую сумму
        BigDecimal newCurrentAmount = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(newCurrentAmount);
        
        // Проверяем, достигнута ли цель
        if (newCurrentAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
            goal.setCurrentAmount(goal.getTargetAmount()); // Устанавливаем точную целевую сумму
            log.info("Цель достигнута: {}", goal.getTitle());
        }
        
        FinancialGoal updatedGoal = goalRepository.save(goal);
        log.info("Внесено {} в цель: {}", amount, goal.getTitle());
        return updatedGoal;
    }
    
    @Override
    public void deleteGoal(Long goalId, User user) {
        FinancialGoal goal = getGoalById(goalId, user);
        
        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new RuntimeException("Нельзя удалить завершенную цель");
        }
        
        goalRepository.delete(goal);
        log.info("Удалена финансовая цель: {}", goalId);
    }
}


package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.model.FinancialGoal;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.GoalStatus;

import java.math.BigDecimal;
import java.util.List;

public interface FinancialGoalService {
    FinancialGoal createGoal(FinancialGoal goal);
    FinancialGoal updateGoal(FinancialGoal goal);
    FinancialGoal getGoalById(Long goalId, User user);
    List<FinancialGoal> getUserGoals(User user);
    List<FinancialGoal> getGoalsByStatus(GoalStatus status, User user);
    FinancialGoal contributeToGoal(Long goalId, BigDecimal amount, User user);
    void deleteGoal(Long goalId, User user);
}


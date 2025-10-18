package com.zamanbank.aiassistant.repository;

import com.zamanbank.aiassistant.model.FinancialGoal;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    
    List<FinancialGoal> findByUserOrderByCreatedAtDesc(User user);
    
    List<FinancialGoal> findByUserAndStatus(User user, GoalStatus status);
    
    @Query("SELECT fg FROM FinancialGoal fg WHERE fg.user = :user AND fg.status = 'IN_PROGRESS' ORDER BY fg.priority DESC, fg.targetDate ASC")
    List<FinancialGoal> findActiveGoalsByUser(@Param("user") User user);
    
    @Query("SELECT fg FROM FinancialGoal fg WHERE fg.user = :user AND fg.targetDate <= CURRENT_DATE AND fg.status = 'IN_PROGRESS'")
    List<FinancialGoal> findOverdueGoalsByUser(@Param("user") User user);
}

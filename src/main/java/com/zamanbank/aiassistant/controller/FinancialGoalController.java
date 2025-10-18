package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.GoalDto;
import com.zamanbank.aiassistant.model.FinancialGoal;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.GoalStatus;
import com.zamanbank.aiassistant.model.enums.GoalType;
import com.zamanbank.aiassistant.model.enums.GoalPriority;
import com.zamanbank.aiassistant.service.FinancialGoalService;
import com.zamanbank.aiassistant.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Slf4j
public class FinancialGoalController {
    
    private final FinancialGoalService goalService;
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<GoalDto> createGoal(
            @RequestBody CreateGoalRequest request,
            Authentication authentication) {
        
        try {
            User user = userService.getCurrentUser(authentication);
            
            FinancialGoal goal = FinancialGoal.builder()
                    .user(user)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .type(request.getType())
                    .priority(GoalPriority.valueOf(request.getPriority()))
                    .status(GoalStatus.PLANNING)
                    .targetAmount(request.getTargetAmount())
                    .targetDate(request.getTargetDate())
                    .monthlyContribution(request.getMonthlyContribution())
                    .build();
            
            FinancialGoal savedGoal = goalService.createGoal(goal);
            GoalDto goalDto = convertToDto(savedGoal);
            return ResponseEntity.ok(goalDto);
            
        } catch (Exception e) {
            log.error("Ошибка при создании цели", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<GoalDto>> getUserGoals(Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            List<FinancialGoal> goals = goalService.getUserGoals(user);
            List<GoalDto> goalDtos = goals.stream()
                    .map(this::convertToDto)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(goalDtos);
        } catch (Exception e) {
            log.error("Ошибка при получении целей", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalDto> getGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            FinancialGoal goal = goalService.getGoalById(goalId, user);
            GoalDto goalDto = convertToDto(goal);
            return ResponseEntity.ok(goalDto);
        } catch (Exception e) {
            log.error("Ошибка при получении цели", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalDto> updateGoal(
            @PathVariable Long goalId,
            @RequestBody UpdateGoalRequest request,
            Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            FinancialGoal goal = goalService.getGoalById(goalId, user);
            
            // Обновляем поля
            if (request.getTitle() != null) goal.setTitle(request.getTitle());
            if (request.getDescription() != null) goal.setDescription(request.getDescription());
            if (request.getTargetAmount() != null) goal.setTargetAmount(request.getTargetAmount());
            if (request.getTargetDate() != null) goal.setTargetDate(request.getTargetDate());
            if (request.getMonthlyContribution() != null) goal.setMonthlyContribution(request.getMonthlyContribution());
            if (request.getStatus() != null) goal.setStatus(request.getStatus());
            
            FinancialGoal updatedGoal = goalService.updateGoal(goal);
            GoalDto goalDto = convertToDto(updatedGoal);
            return ResponseEntity.ok(goalDto);
            
        } catch (Exception e) {
            log.error("Ошибка при обновлении цели", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/{goalId}/contribute")
    public ResponseEntity<GoalDto> contributeToGoal(
            @PathVariable Long goalId,
            @RequestBody ContributionRequest request,
            Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            FinancialGoal goal = goalService.contributeToGoal(goalId, request.getAmount(), user);
            GoalDto goalDto = convertToDto(goal);
            return ResponseEntity.ok(goalDto);
        } catch (Exception e) {
            log.error("Ошибка при внесении средств в цель", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        try {
            User user = userService.getCurrentUser(authentication);
            goalService.deleteGoal(goalId, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при удалении цели", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // DTO классы
    public static class CreateGoalRequest {
        private String title;
        private String description;
        private GoalType type;
        private String priority;
        private BigDecimal targetAmount;
        private LocalDate targetDate;
        private BigDecimal monthlyContribution;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public GoalType getType() { return type; }
        public void setType(GoalType type) { this.type = type; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public BigDecimal getTargetAmount() { return targetAmount; }
        public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
        public LocalDate getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
        public BigDecimal getMonthlyContribution() { return monthlyContribution; }
        public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }
    }
    
    public static class UpdateGoalRequest {
        private String title;
        private String description;
        private BigDecimal targetAmount;
        private LocalDate targetDate;
        private BigDecimal monthlyContribution;
        private GoalStatus status;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getTargetAmount() { return targetAmount; }
        public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
        public LocalDate getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
        public BigDecimal getMonthlyContribution() { return monthlyContribution; }
        public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }
        public GoalStatus getStatus() { return status; }
        public void setStatus(GoalStatus status) { this.status = status; }
    }
    
    public static class ContributionRequest {
        private BigDecimal amount;
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
    
    private GoalDto convertToDto(FinancialGoal goal) {
        return GoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .type(goal.getType())
                .priority(goal.getPriority())
                .status(goal.getStatus())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .monthlyContribution(goal.getMonthlyContribution())
                .targetDate(goal.getTargetDate())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .progressPercentage(goal.getProgressPercentage())
                .daysRemaining(goal.getDaysRemaining())
                .contributionHistory(java.util.Collections.emptyList()) // Mock data
                .build();
    }
}

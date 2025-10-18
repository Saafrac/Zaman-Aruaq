package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.GoalRecommendation;
import com.zamanbank.aiassistant.model.FinancialGoal;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.GoalStatus;
import com.zamanbank.aiassistant.model.enums.GoalType;
import com.zamanbank.aiassistant.model.enums.GoalPriority;
import com.zamanbank.aiassistant.service.FinancialGoalService;
import com.zamanbank.aiassistant.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
@Slf4j
public class FinancialGoalController {
    
    private final FinancialGoalService goalService;
    private final UserService userService;
    
    @GetMapping("/test")
    public ResponseEntity<String> testGoals() {
        return ResponseEntity.ok("Financial Goals API работает!");
    }
    
    @Getter @Setter
    public static class CreateGoalRequest {
        private String title;
        private String description;
        private BigDecimal targetAmount;
        private LocalDate targetDate;
        private GoalType type;
        private GoalPriority priority;
    }
    
    @Getter @Setter
    public static class UpdateGoalRequest {
        private String title;
        private String description;
        private BigDecimal targetAmount;
        private LocalDate targetDate;
        private GoalStatus status;
        private GoalPriority priority;
    }
}
package com.zamanbank.aiassistant.dto;

import com.zamanbank.aiassistant.model.enums.GoalPriority;
import com.zamanbank.aiassistant.model.enums.GoalStatus;
import com.zamanbank.aiassistant.model.enums.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private GoalType type;
    private GoalPriority priority;
    private GoalStatus status;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal monthlyContribution;
    private LocalDate targetDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Double progressPercentage;
    private Long daysRemaining;
    private List<ContributionHistory> contributionHistory;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContributionHistory {
        private Long id;
        private BigDecimal amount;
        private LocalDate date;
        private String description;
    }
}

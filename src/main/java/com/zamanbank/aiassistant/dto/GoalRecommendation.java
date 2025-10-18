package com.zamanbank.aiassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalRecommendation {
    private Long goalId;
    private String recommendation;
    private String priority;
    private String timeline;
    private String actionPlan;
    private Double confidence;
}


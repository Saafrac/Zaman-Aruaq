package com.zamanbank.aiassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {
    private String content;
    private String intent;
    private String sentiment;
    private Double confidence;
    private String suggestedActions;
    private List<String> quickReplies;
    private Boolean requiresFollowUp;
}

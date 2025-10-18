package com.zamanbank.aiassistant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendation {
    private String productName;
    private String productType;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String term;
    private Double suitability;
    private String benefits;
    private String requirements;
}

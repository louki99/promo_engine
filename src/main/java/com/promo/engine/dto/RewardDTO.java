package com.promo.engine.dto;

import com.promo.engine.enums.RewardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RewardDTO {
    @NotNull(message = "Reward type is required")
    private RewardType rewardType;
    
    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    private BigDecimal value;
    
    private String targetEntityType; // PRODUCT, PRODUCT_FAMILY
    
    private Long targetEntityId;
    
    private String description;
    
    private BigDecimal maxDiscount;
} 
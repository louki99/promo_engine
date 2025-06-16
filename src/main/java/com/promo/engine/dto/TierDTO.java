package com.promo.engine.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TierDTO {
    @NotNull(message = "Rule ID is required")
    private Long ruleId;

    @NotNull(message = "Minimum threshold is required")
    @Positive(message = "Minimum threshold must be positive")
    private BigDecimal minimumThreshold;

    @NotNull(message = "Reward is required")
    private RewardDTO reward;
} 
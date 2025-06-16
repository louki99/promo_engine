package com.promo.engine.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppliedDiscount {
    private String promotionCode;
    private String description;
    private BigDecimal amount;
    private String type;
} 
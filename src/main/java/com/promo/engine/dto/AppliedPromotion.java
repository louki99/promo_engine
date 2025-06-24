package com.promo.engine.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AppliedPromotion {
    private Long promotionId;
    private String promoCode;
    private String name;
    private String description;
    private BigDecimal discountAmount;
    private String stackingGroup;
} 
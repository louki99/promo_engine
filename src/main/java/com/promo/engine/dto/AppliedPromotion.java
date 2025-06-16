package com.promo.engine.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AppliedPromotion {
    private String promoCode;
    private String description;
    private BigDecimal discountAmount;
} 
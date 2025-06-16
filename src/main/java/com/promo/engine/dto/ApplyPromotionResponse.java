package com.promo.engine.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ApplyPromotionResponse {
    private List<LineItem> lineItems = new ArrayList<>();
    private List<FreeItem> freeItems = new ArrayList<>();
    private List<AppliedPromotion> appliedPromotions = new ArrayList<>();
    private BigDecimal originalTotal;
    private BigDecimal discountTotal;
    private BigDecimal finalTotal;
    private BigDecimal shippingDiscount;
    private Integer loyaltyPointsEarned;
} 
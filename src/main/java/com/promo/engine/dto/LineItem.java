package com.promo.engine.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Data
public class LineItem {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal originalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal finalPrice;
    private List<AppliedDiscount> appliedDiscounts = new ArrayList<>();
}
package com.promo.engine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ApplyPromotionRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotEmpty(message = "Cart items cannot be empty")
    @Valid
    private List<CartItem> cartItems;
    
    private String promoCode;
    
    private String locationId;
    
    private String customerSegment;
    
    private boolean isFirstPurchase;
    
    private int loyaltyPoints;
} 
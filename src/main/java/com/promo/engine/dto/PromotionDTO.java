package com.promo.engine.dto;

import com.promo.engine.enums.PromotionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionDTO {
    @NotBlank(message = "Promo code is required")
    private String promoCode;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Promotion type is required")
    private PromotionType type;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    private boolean isActive = true;
    
    private int priority = 0;
    
    private boolean isExclusive = false;
    
    private String combinabilityGroup;
    
    private BigDecimal maxDiscount;
    
    private Integer maxUsagePerCustomer;
    
    private Integer maxTotalUsage;
    
    private boolean exclusive = false;
    
    private String stackingGroup;
    
    private Integer maxStackCount;
    
    private Integer maxStackPerCustomer;
    
    private Integer maxStackPerOrder;
} 
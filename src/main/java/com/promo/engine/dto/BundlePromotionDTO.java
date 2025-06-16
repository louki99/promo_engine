package com.promo.engine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BundlePromotionDTO {
    private Long id;
    
    @NotEmpty(message = "Required items cannot be empty")
    @Valid
    private List<BundleItemDTO> requiredItems;
    
    @Valid
    private List<BundleItemDTO> rewardItems;
    
    @NotNull(message = "Bundle discount is required")
    @Positive(message = "Bundle discount must be positive")
    private BigDecimal bundleDiscount;
    
    private Integer maxBundles;
    
    private Boolean isActive = true;
    
    private Long promotionId;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BundleItemDTO {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        private Integer minQuantity;
        
        private Integer maxQuantity;
        
        private Boolean isRequired = true;
    }
}
package com.promo.engine.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "cross_promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrossPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @ElementCollection
    @CollectionTable(name = "cross_promotion_trigger_products")
    @Column(name = "product_id")
    private List<Long> triggerProductIds;
    
    @ElementCollection
    @CollectionTable(name = "cross_promotion_target_products")
    @Column(name = "product_id")
    private List<Long> targetProductIds;
    
    @ElementCollection
    @CollectionTable(name = "cross_promotion_category_rules")
    @MapKeyColumn(name = "category_id")
    @Column(name = "discount_percentage")
    private Map<Long, BigDecimal> categoryDiscounts;
    
    @Column(name = "min_trigger_quantity")
    private Integer minTriggerQuantity;
    
    @Column(name = "max_target_quantity")
    private Integer maxTargetQuantity;
    
    @Column(name = "discount_type")
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    
    @Column(name = "discount_value")
    private BigDecimal discountValue;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    
    @OneToOne
    @JoinColumn(name = "time_condition_id")
    private TimeBasedCondition timeCondition;
    
    @ElementCollection
    @CollectionTable(name = "cross_promotion_exclusions")
    @Column(name = "product_id")
    private List<Long> excludedProductIds;
    
    @Column(name = "stacking_allowed")
    private Boolean stackingAllowed = false;
    
    @Column(name = "max_stack_count")
    private Integer maxStackCount;
    
    @Column(name = "priority")
    private Integer priority;
    
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT,
        FREE_PRODUCT,
        BUNDLE_DISCOUNT
    }
    
    public boolean isApplicable(List<Long> cartProductIds, Map<Long, Integer> productQuantities) {
        if (!isActive) {
            return false;
        }
        
        // Check if time condition is applicable
        if (timeCondition != null && !timeCondition.isApplicable()) {
            return false;
        }
        
        // Check if trigger products are in cart with sufficient quantity
        boolean hasTriggerProducts = triggerProductIds.stream()
                .anyMatch(productId -> {
                    Integer quantity = productQuantities.getOrDefault(productId, 0);
                    return quantity >= minTriggerQuantity;
                });
        
        if (!hasTriggerProducts) {
            return false;
        }
        
        // Check if target products are in cart
        boolean hasTargetProducts = targetProductIds.stream()
                .anyMatch(cartProductIds::contains);
        
        return hasTargetProducts;
    }
    
    public BigDecimal calculateDiscount(Long productId, BigDecimal originalPrice, int quantity) {
        if (!isActive || excludedProductIds.contains(productId)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        switch (discountType) {
            case PERCENTAGE:
                discount = originalPrice.multiply(discountValue.divide(BigDecimal.valueOf(100)));
                break;
            case FIXED_AMOUNT:
                discount = discountValue;
                break;
            case BUNDLE_DISCOUNT:
                // Calculate bundle discount based on the number of complete bundles
                int bundleCount = Math.min(quantity, maxTargetQuantity);
                discount = discountValue.multiply(BigDecimal.valueOf(bundleCount));
                break;
            case FREE_PRODUCT:
                // For free product, return the full price of one item
                discount = originalPrice;
                break;
        }
        
        return discount;
    }
    
    public boolean canStackWith(CrossPromotion other) {
        if (!stackingAllowed || !other.getStackingAllowed()) {
            return false;
        }
        
        if (maxStackCount != null && other.getMaxStackCount() != null) {
            return Math.min(maxStackCount, other.getMaxStackCount()) > 1;
        }
        
        return true;
    }
} 
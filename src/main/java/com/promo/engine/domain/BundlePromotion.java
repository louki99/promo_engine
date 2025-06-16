package com.promo.engine.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bundle_promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BundlePromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "bundle_required_items",
        joinColumns = @JoinColumn(name = "bundle_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<BundleItem> requiredItems;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
        name = "bundle_reward_items",
        joinColumns = @JoinColumn(name = "bundle_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<BundleItem> rewardItems;
    
    @Column(name = "bundle_discount")
    private BigDecimal bundleDiscount;
    
    @Column(name = "max_bundles")
    private Integer maxBundles;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BundleItem {
        @Column(nullable = false)
        private Long productId;
        
        @Column(nullable = false)
        private Integer quantity;
        
        @Column(name = "min_quantity")
        private Integer minQuantity;
        
        @Column(name = "max_quantity")
        private Integer maxQuantity;
        
        @Column(name = "is_required")
        private Boolean isRequired = true;
    }
    
    public boolean isBundleComplete(List<BundleItem> cartItems) {
        for (BundleItem required : requiredItems) {
            boolean found = false;
            for (BundleItem cart : cartItems) {
                if (cart.getProductId().equals(required.getProductId()) &&
                    cart.getQuantity() >= required.getQuantity()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    public BigDecimal calculateBundleDiscount(List<BundleItem> cartItems) {
        if (!isBundleComplete(cartItems)) {
            return BigDecimal.ZERO;
        }
        
        int bundleCount = calculateBundleCount(cartItems);
        if (maxBundles != null && bundleCount > maxBundles) {
            bundleCount = maxBundles;
        }
        
        return bundleDiscount.multiply(BigDecimal.valueOf(bundleCount));
    }
    
    private int calculateBundleCount(List<BundleItem> cartItems) {
        int minBundles = Integer.MAX_VALUE;
        
        for (BundleItem required : requiredItems) {
            for (BundleItem cart : cartItems) {
                if (cart.getProductId().equals(required.getProductId())) {
                    int possibleBundles = cart.getQuantity() / required.getQuantity();
                    minBundles = Math.min(minBundles, possibleBundles);
                }
            }
        }
        
        return minBundles == Integer.MAX_VALUE ? 0 : minBundles;
    }
} 
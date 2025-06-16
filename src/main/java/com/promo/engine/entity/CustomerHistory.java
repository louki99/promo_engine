package com.promo.engine.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "customer_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "total_spend")
    private BigDecimal totalSpend;
    
    @Column(name = "total_orders")
    private Integer totalOrders;
    
    @Column(name = "average_order_value")
    private BigDecimal averageOrderValue;
    
    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;
    
    @Column(name = "first_order_date")
    private LocalDateTime firstOrderDate;
    
    @ElementCollection
    @CollectionTable(name = "customer_category_spend")
    @MapKeyColumn(name = "category_id")
    @Column(name = "spend_amount")
    private Map<Long, BigDecimal> categorySpend;
    
    @ElementCollection
    @CollectionTable(name = "customer_product_history")
    private List<ProductPurchase> productHistory;
    
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints;
    
    @Column(name = "loyalty_tier")
    private String loyaltyTier;
    
    @Column(name = "last_segment_update")
    private LocalDateTime lastSegmentUpdate;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductPurchase {
        @Column(name = "product_id", nullable = false)
        private Long productId;
        
        @Column(name = "purchase_date", nullable = false)
        private LocalDateTime purchaseDate;
        
        @Column(name = "quantity", nullable = false)
        private Integer quantity;
        
        @Column(name = "unit_price", nullable = false)
        private BigDecimal unitPrice;
        
        @Column(name = "total_price", nullable = false)
        private BigDecimal totalPrice;
        
        @Column(name = "category_id")
        private Long categoryId;
    }
    
    public void updateAfterOrder(BigDecimal orderTotal, List<ProductPurchase> newPurchases) {
        this.totalOrders++;
        this.totalSpend = this.totalSpend.add(orderTotal);
        this.averageOrderValue = this.totalSpend.divide(BigDecimal.valueOf(this.totalOrders), 2, BigDecimal.ROUND_HALF_UP);
        this.lastOrderDate = LocalDateTime.now();
        
        if (this.firstOrderDate == null) {
            this.firstOrderDate = this.lastOrderDate;
        }
        
        // Update category spend
        for (ProductPurchase purchase : newPurchases) {
            if (purchase.getCategoryId() != null) {
                BigDecimal currentSpend = this.categorySpend.getOrDefault(purchase.getCategoryId(), BigDecimal.ZERO);
                this.categorySpend.put(purchase.getCategoryId(), currentSpend.add(purchase.getTotalPrice()));
            }
        }
        
        // Update product history
        this.productHistory.addAll(newPurchases);
    }
    
    public boolean isEligibleForSegment(CustomerSegment segment) {
        return segment.isCustomerEligible(this.customerId, this.totalSpend, this.totalOrders);
    }
    
    public void updateLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        // Logic to update loyalty tier based on points could be added here
    }
} 
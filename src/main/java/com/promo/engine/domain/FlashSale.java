package com.promo.engine.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Table(name = "flash_sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "early_bird_discount")
    private BigDecimal earlyBirdDiscount;
    
    @Column(name = "early_bird_end_time")
    private LocalDateTime earlyBirdEndTime;
    
    @Column(name = "max_purchase_per_customer")
    private Integer maxPurchasePerCustomer;
    
    @Column(name = "min_purchase_amount")
    private BigDecimal minPurchaseAmount;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.SCHEDULED;
    
    @OneToMany(mappedBy = "flashSale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlashSaleInventory> inventory = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "flash_sale_demand_factors")
    @MapKeyColumn(name = "product_id")
    @Column(name = "factor")
    private Map<Long, Double> demandFactors = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "flash_sale_loyalty_multipliers")
    @MapKeyColumn(name = "loyalty_tier")
    @Column(name = "multiplier")
    private Map<String, Double> loyaltyMultipliers = new HashMap<>();
    
    @ElementCollection
    @CollectionTable(name = "flash_sale_bulk_discounts")
    @MapKeyColumn(name = "min_quantity")
    @Column(name = "discount")
    private Map<Integer, BigDecimal> bulkDiscounts = new HashMap<>();
    
    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    
    public enum Status {
        SCHEDULED,
        ACTIVE,
        PAUSED,
        ENDED,
        CANCELLED
    }
    
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               status == Status.ACTIVE && 
               now.isAfter(startTime) && 
               now.isBefore(endTime);
    }
    
    public boolean isEarlyBird() {
        LocalDateTime now = LocalDateTime.now();
        return earlyBirdEndTime != null && 
               now.isAfter(startTime) && 
               now.isBefore(earlyBirdEndTime);
    }
    
    public BigDecimal getApplicableDiscount() {
        if (isEarlyBird() && earlyBirdDiscount != null) {
            return earlyBirdDiscount;
        }
        return BigDecimal.ZERO;
    }
    
    public double getLoyaltyMultiplier(String loyaltyTier) {
        return loyaltyMultipliers.getOrDefault(loyaltyTier, 1.0);
    }
    
    public BigDecimal getBulkDiscount(int quantity) {
        return bulkDiscounts.entrySet().stream()
                .filter(entry -> quantity >= entry.getKey())
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(BigDecimal.ZERO);
    }
    
    public double getDemandFactor(Long productId) {
        return demandFactors.getOrDefault(productId, 1.0);
    }
    
    public void updateStatus(Status newStatus) {
        this.status = newStatus;
        if (newStatus == Status.ENDED || newStatus == Status.CANCELLED) {
            this.isActive = false;
        }
    }
    
    public void extendDuration(int additionalMinutes) {
        this.endTime = this.endTime.plusMinutes(additionalMinutes);
    }
} 
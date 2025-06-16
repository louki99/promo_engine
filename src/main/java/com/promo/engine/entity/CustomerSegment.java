package com.promo.engine.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "customer_segments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String segmentId;
    
    @Column(nullable = false)
    private String name;
    
    @ElementCollection
    @CollectionTable(name = "segment_attributes")
    private List<CustomerAttribute> attributes;
    
    @Column(name = "min_spend")
    private BigDecimal minSpend;
    
    @Column(name = "min_orders")
    private Integer minOrders;
    
    @Column(name = "member_since")
    private LocalDate memberSince;
    
    @Column(name = "loyalty_level")
    private Integer loyaltyLevel;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerAttribute {
        @Column(nullable = false)
        private String name;
        
        @Column(nullable = false)
        private String value;
        
        @Column(nullable = false)
        private String operator; // EQUALS, GREATER_THAN, LESS_THAN, etc.
    }
    
    public boolean isCustomerEligible(Long customerId, BigDecimal totalSpend, Integer totalOrders) {
        if (!isActive) {
            return false;
        }
        
        if (minSpend != null && totalSpend.compareTo(minSpend) < 0) {
            return false;
        }
        
        if (minOrders != null && totalOrders < minOrders) {
            return false;
        }
        
        return true;
    }
} 
package com.promo.engine.domain;

import com.promo.engine.enums.RewardType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardType rewardType;
    
    @Column(nullable = false)
    private BigDecimal value;
    
    private BigDecimal maxDiscount;
    
    private String targetEntityType; // PRODUCT, PRODUCT_FAMILY
    
    private Long targetEntityId;
    
    private String description;
} 
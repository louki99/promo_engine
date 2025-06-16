package com.promo.engine.domain;

import com.promo.engine.enums.ConditionType;
import com.promo.engine.enums.OperatorType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private PromotionRule rule;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionType conditionType;
    
    @Enumerated(EnumType.STRING)
    private OperatorType operator = OperatorType.GREATER_THAN_OR_EQUAL;
    
    private String value;
    
    private String entityType; // PRODUCT, PRODUCT_FAMILY, CUSTOMER_FAMILY
    
    private Long entityId;
} 
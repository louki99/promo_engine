package com.promo.engine.entity;

import com.promo.engine.enums.BreakpointType;
import com.promo.engine.enums.CalculationMethod;
import com.promo.engine.enums.ConditionLogic;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promotion_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private ConditionLogic conditionLogic = ConditionLogic.ALL;
    
    @Enumerated(EnumType.STRING)
    private CalculationMethod calculationMethod = CalculationMethod.BRACKET;
    
    @Enumerated(EnumType.STRING)
    private BreakpointType breakpointType = BreakpointType.AMOUNT;
    
    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Condition> conditions;
    
    @OneToMany(mappedBy = "rule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tier> tiers;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
} 
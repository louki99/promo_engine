package com.promo.engine.domain;

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
@Table(name = "rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleEntity extends PromotionRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private PromotionEntity promotion;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "jsonb")
    private String parameters;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Promotion getPromotion() {
        return promotion;
    }

    @Override
    public void setPromotion(Promotion promotion) {
        if (promotion instanceof PromotionEntity) {
            this.promotion = (PromotionEntity) promotion;
        } else {
            throw new IllegalArgumentException("Expected PromotionEntity but got: " + promotion.getClass());
        }
    }
} 
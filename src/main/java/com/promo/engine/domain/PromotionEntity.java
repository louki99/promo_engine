package com.promo.engine.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionEntity extends Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "promo_code", unique = true)
    private String promoCode;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private int priority;

    @Column(name = "is_exclusive")
    private boolean exclusive;

    @Column(name = "stacking_group")
    private String stackingGroup;

    @Column(name = "max_stack_count")
    private Integer maxStackCount;

    @Column(name = "max_stack_per_customer")
    private Integer maxStackPerCustomer;

    @Column(name = "max_stack_per_order")
    private Integer maxStackPerOrder;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RuleEntity> rules = new HashSet<>();

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ActionEntity> actions = new HashSet<>();

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
    public List<PromotionRule> getRules() {
        return new ArrayList<>(rules);
    }

    @Override
    public void setRules(List<PromotionRule> rules) {
        // Convert List<PromotionRule> to Set<RuleEntity>
        this.rules.clear();
        for (PromotionRule rule : rules) {
            if (rule instanceof RuleEntity) {
                this.rules.add((RuleEntity) rule);
            } else {
                throw new IllegalArgumentException("Expected RuleEntity but got: " + rule.getClass());
            }
        }
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority != null ? priority : 0;
    }
} 
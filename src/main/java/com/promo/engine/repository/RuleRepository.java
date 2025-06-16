package com.promo.engine.repository;

import com.promo.engine.entity.PromotionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<PromotionRule, Long> {
    List<PromotionRule> findByPromotionId(Long promotionId);
} 
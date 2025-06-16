package com.promo.engine.repository;

import com.promo.engine.domain.PromotionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRuleRepository extends JpaRepository<PromotionRule, Long> {
} 
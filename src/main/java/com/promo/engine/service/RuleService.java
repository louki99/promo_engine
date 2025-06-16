package com.promo.engine.service;

import com.promo.engine.dto.ConditionDTO;
import com.promo.engine.dto.RuleDTO;
import com.promo.engine.dto.TierDTO;
import com.promo.engine.entity.PromotionRule;

import java.util.List;

public interface RuleService {
    PromotionRule createRule(RuleDTO ruleDTO);
    
    List<PromotionRule> getAllRules();
    
    PromotionRule getRuleById(Long id);
    
    List<PromotionRule> getRulesByPromotionId(Long promotionId);
    
    PromotionRule updateRule(Long id, RuleDTO ruleDTO);
    
    void deleteRule(Long id);
    
    PromotionRule addCondition(Long ruleId, ConditionDTO conditionDTO);
    
    PromotionRule removeCondition(Long ruleId, Long conditionId);
    
    PromotionRule addTier(Long ruleId, TierDTO tierDTO);
    
    PromotionRule removeTier(Long ruleId, Long tierId);
} 
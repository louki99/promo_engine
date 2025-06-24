package com.promo.engine.service;

import com.promo.engine.dto.ConditionDTO;
import com.promo.engine.dto.RuleDTO;
import com.promo.engine.dto.TierDTO;
import com.promo.engine.domain.RuleEntity;
import java.util.List;

public interface RuleService {
    RuleEntity createRule(RuleDTO ruleDTO);
    List<RuleEntity> getAllRules();
    RuleEntity getRuleById(Long id);
    List<RuleEntity> getRulesByPromotionId(Long promotionId);
    RuleEntity updateRule(Long id, RuleDTO ruleDTO);
    void deleteRule(Long id);

    RuleEntity addCondition(Long ruleId, ConditionDTO conditionDTO);
    RuleEntity removeCondition(Long ruleId, Long conditionId);
    RuleEntity addTier(Long ruleId, TierDTO tierDTO);
    RuleEntity removeTier(Long ruleId, Long tierId);
} 
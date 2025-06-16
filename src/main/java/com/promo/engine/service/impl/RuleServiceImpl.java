package com.promo.engine.service.impl;

import com.promo.engine.dto.ConditionDTO;
import com.promo.engine.dto.RuleDTO;
import com.promo.engine.dto.TierDTO;
import com.promo.engine.entity.Condition;
import com.promo.engine.entity.Promotion;
import com.promo.engine.entity.PromotionRule;
import com.promo.engine.entity.Reward;
import com.promo.engine.entity.Tier;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.ConditionRepository;
import com.promo.engine.repository.PromotionRepository;
import com.promo.engine.repository.RewardRepository;
import com.promo.engine.repository.RuleRepository;
import com.promo.engine.repository.TierRepository;
import com.promo.engine.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final RuleRepository ruleRepository;
    private final PromotionRepository promotionRepository;
    private final ConditionRepository conditionRepository;
    private final TierRepository tierRepository;
    private final RewardRepository rewardRepository;

    @Override
    @Transactional
    public PromotionRule createRule(RuleDTO ruleDTO) {
        Promotion promotion = promotionRepository.findById(ruleDTO.getPromotionId())
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        PromotionRule rule = new PromotionRule();
        rule.setPromotion(promotion);
        rule.setName(ruleDTO.getName());
        rule.setConditionLogic(ruleDTO.getConditionLogic());
        rule.setCalculationMethod(ruleDTO.getCalculationMethod());
        rule.setBreakpointType(ruleDTO.getBreakpointType());

        return ruleRepository.save(rule);
    }

    @Override
    public List<PromotionRule> getAllRules() {
        return ruleRepository.findAll();
    }

    @Override
    public PromotionRule getRuleById(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
    }

    @Override
    public List<PromotionRule> getRulesByPromotionId(Long promotionId) {
        return ruleRepository.findByPromotionId(promotionId);
    }

    @Override
    @Transactional
    public PromotionRule updateRule(Long id, RuleDTO ruleDTO) {
        PromotionRule rule = getRuleById(id);
        
        if (ruleDTO.getPromotionId() != null && !ruleDTO.getPromotionId().equals(rule.getPromotion().getId())) {
            Promotion promotion = promotionRepository.findById(ruleDTO.getPromotionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
            rule.setPromotion(promotion);
        }
        
        rule.setName(ruleDTO.getName());
        rule.setConditionLogic(ruleDTO.getConditionLogic());
        rule.setCalculationMethod(ruleDTO.getCalculationMethod());
        rule.setBreakpointType(ruleDTO.getBreakpointType());

        return ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        PromotionRule rule = getRuleById(id);
        ruleRepository.delete(rule);
    }

    @Override
    @Transactional
    public PromotionRule addCondition(Long ruleId, ConditionDTO conditionDTO) {
        PromotionRule rule = getRuleById(ruleId);
        
        Condition condition = new Condition();
        condition.setRule(rule);
        condition.setConditionType(conditionDTO.getConditionType());
        condition.setOperator(conditionDTO.getOperator());
        condition.setValue(conditionDTO.getValue());
        condition.setEntityType(conditionDTO.getEntityType());
        condition.setEntityId(conditionDTO.getEntityId());
        
        conditionRepository.save(condition);
        return rule;
    }

    @Override
    @Transactional
    public PromotionRule removeCondition(Long ruleId, Long conditionId) {
        PromotionRule rule = getRuleById(ruleId);
        Condition condition = conditionRepository.findById(conditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Condition not found"));
        
        if (!condition.getRule().getId().equals(ruleId)) {
            throw new IllegalArgumentException("Condition does not belong to the specified rule");
        }
        
        conditionRepository.delete(condition);
        return rule;
    }

    @Override
    @Transactional
    public PromotionRule addTier(Long ruleId, TierDTO tierDTO) {
        PromotionRule rule = getRuleById(ruleId);
        
        // Create and save the reward first
        Reward reward = new Reward();
        reward.setRewardType(tierDTO.getReward().getRewardType());
        reward.setValue(tierDTO.getReward().getValue());
        reward.setTargetEntityType(tierDTO.getReward().getTargetEntityType());
        reward.setTargetEntityId(tierDTO.getReward().getTargetEntityId());
        reward.setDescription(tierDTO.getReward().getDescription());
        reward.setMaxDiscount(tierDTO.getReward().getMaxDiscount());
        reward = rewardRepository.save(reward);
        
        // Create and save the tier
        Tier tier = new Tier();
        tier.setRule(rule);
        tier.setMinimumThreshold(tierDTO.getMinimumThreshold());
        tier.setReward(reward);
        
        tierRepository.save(tier);
        return rule;
    }

    @Override
    @Transactional
    public PromotionRule removeTier(Long ruleId, Long tierId) {
        PromotionRule rule = getRuleById(ruleId);
        Tier tier = tierRepository.findById(tierId)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));
        
        if (!tier.getRule().getId().equals(ruleId)) {
            throw new IllegalArgumentException("Tier does not belong to the specified rule");
        }
        
        tierRepository.delete(tier);
        return rule;
    }
} 
package com.promo.engine.service.impl;

import com.promo.engine.dto.TierDTO;
import com.promo.engine.domain.PromotionRule;
import com.promo.engine.domain.Reward;
import com.promo.engine.domain.Tier;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.PromotionRuleRepository;
import com.promo.engine.repository.RewardRepository;
import com.promo.engine.repository.TierRepository;
import com.promo.engine.service.TierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierServiceImpl implements TierService {
    private final TierRepository tierRepository;
    private final PromotionRuleRepository ruleRepository;
    private final RewardRepository rewardRepository;

    @Override
    @Transactional
    public Tier createTier(TierDTO tierDTO) {
        PromotionRule rule = ruleRepository.findById(tierDTO.getRuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
        
        Reward reward = new Reward();
        reward.setRewardType(tierDTO.getReward().getRewardType());
        reward.setValue(tierDTO.getReward().getValue());
        reward.setTargetEntityType(tierDTO.getReward().getTargetEntityType());
        reward.setTargetEntityId(tierDTO.getReward().getTargetEntityId());
        reward.setDescription(tierDTO.getReward().getDescription());
        reward.setMaxDiscount(tierDTO.getReward().getMaxDiscount());
        reward = rewardRepository.save(reward);

        Tier tier = new Tier();
        tier.setRule(rule);
        tier.setMinimumThreshold(tierDTO.getMinimumThreshold());
        tier.setReward(reward);
        return tierRepository.save(tier);
    }

    @Override
    public Tier getTierById(Long id) {
        return tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));
    }

    @Override
    public List<Tier> getAllTiers() {
        return tierRepository.findAll();
    }

    @Override
    @Transactional
    public Tier updateTier(Long id, TierDTO tierDTO) {
        Tier tier = getTierById(id);
        if (tierDTO.getRuleId() != null && !tierDTO.getRuleId().equals(tier.getRule().getId())) {
            PromotionRule rule = ruleRepository.findById(tierDTO.getRuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
            tier.setRule(rule);
        }
        
        if (tierDTO.getReward() != null) {
            Reward reward = tier.getReward();
            reward.setRewardType(tierDTO.getReward().getRewardType());
            reward.setValue(tierDTO.getReward().getValue());
            reward.setTargetEntityType(tierDTO.getReward().getTargetEntityType());
            reward.setTargetEntityId(tierDTO.getReward().getTargetEntityId());
            reward.setDescription(tierDTO.getReward().getDescription());
            reward.setMaxDiscount(tierDTO.getReward().getMaxDiscount());
            reward = rewardRepository.save(reward);
            tier.setReward(reward);
        }
        
        tier.setMinimumThreshold(tierDTO.getMinimumThreshold());
        return tierRepository.save(tier);
    }

    @Override
    @Transactional
    public void deleteTier(Long id) {
        Tier tier = getTierById(id);
        tierRepository.delete(tier);
    }
} 
package com.promo.engine.service;

import com.promo.engine.domain.PromotionEntity;
import com.promo.engine.domain.RuleEntity;
import com.promo.engine.domain.ActionEntity;
import com.promo.engine.domain.PromotionRule;
import com.promo.engine.dto.CreatePromotionRequest;
import com.promo.engine.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionManagementService {
    private final PromotionRepository promotionRepository;

    @Transactional
    public PromotionEntity createPromotion(CreatePromotionRequest request) {
        PromotionEntity promotion = new PromotionEntity();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.isActive());
        promotion.setPriority(request.getPriority());

        // Create rules
        var rules = request.getRules().stream()
            .map(ruleDto -> {
                RuleEntity rule = new RuleEntity();
                rule.setType(ruleDto.getType());
                rule.setParameters(ruleDto.getParameters());
                rule.setPromotion(promotion);
                return rule;
            })
            .collect(Collectors.toCollection(HashSet::new));
        
        // Convert HashSet<RuleEntity> to List<PromotionRule>
        List<PromotionRule> rulesList = new ArrayList<>(rules);
        promotion.setRules(rulesList);

        // Create actions
        var actions = request.getActions().stream()
            .map(actionDto -> {
                ActionEntity action = new ActionEntity();
                action.setType(actionDto.getType());
                action.setParameters(actionDto.getParameters());
                action.setPromotion(promotion);
                return action;
            })
            .collect(Collectors.toCollection(HashSet::new));
        promotion.setActions(actions);

        return promotionRepository.save(promotion);
    }

    @Transactional(readOnly = true)
    public PromotionEntity getPromotion(Long id) {
        return promotionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Promotion not found: " + id));
    }

    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    @Transactional
    public PromotionEntity updatePromotion(Long id, CreatePromotionRequest request) {
        PromotionEntity promotion = getPromotion(id);
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.isActive());
        promotion.setPriority(request.getPriority());

        // Update rules
        promotion.getRules().clear();
        var rules = request.getRules().stream()
            .map(ruleDto -> {
                RuleEntity rule = new RuleEntity();
                rule.setType(ruleDto.getType());
                rule.setParameters(ruleDto.getParameters());
                rule.setPromotion(promotion);
                return rule;
            })
            .collect(Collectors.toCollection(HashSet::new));
        
        // Convert HashSet<RuleEntity> to List<PromotionRule>
        List<PromotionRule> rulesList = new ArrayList<>(rules);
        promotion.setRules(rulesList);

        // Update actions
        promotion.getActions().clear();
        var actions = request.getActions().stream()
            .map(actionDto -> {
                ActionEntity action = new ActionEntity();
                action.setType(actionDto.getType());
                action.setParameters(actionDto.getParameters());
                action.setPromotion(promotion);
                return action;
            })
            .collect(Collectors.toCollection(HashSet::new));
        promotion.setActions(actions);

        return promotionRepository.save(promotion);
    }
} 
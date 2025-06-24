package com.promo.engine.service;

import com.promo.engine.domain.Cart;
import com.promo.engine.domain.PromotionEntity;
import com.promo.engine.domain.RuleEntity;
import com.promo.engine.factory.RuleFactory;
import com.promo.engine.factory.ActionFactory;
import com.promo.engine.repository.PromotionRepository;
import com.promo.engine.rule.Rule;
import com.promo.engine.action.Action;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionEngine {
    private final PromotionRepository promotionRepository;
    private final RuleFactory ruleFactory;
    private final ActionFactory actionFactory;

    @Transactional(readOnly = true)
    public void applyPromotions(Cart cart) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get active promotions ordered by priority
        List<PromotionEntity> activePromotions = promotionRepository
            .findByActiveIsTrueAndStartDateBeforeAndEndDateAfterOrderByPriorityDesc(now, now);

        // Process each promotion
        for (PromotionEntity promotion : activePromotions) {
            // Convert rules and check if all rules are satisfied
            boolean allRulesSatisfied = promotion.getRules().stream()
                .filter(rule -> rule instanceof RuleEntity)
                .map(rule -> (RuleEntity) rule)
                .map(ruleFactory::createRule)
                .allMatch(rule -> rule.evaluate(cart));

            // If all rules are satisfied, apply all actions
            if (allRulesSatisfied) {
                promotion.getActions().stream()
                    .map(actionFactory::createAction)
                    .forEach(action -> action.apply(cart));
                
                // Stop processing after first matching promotion (highest priority wins)
                break;
            }
        }
    }
} 
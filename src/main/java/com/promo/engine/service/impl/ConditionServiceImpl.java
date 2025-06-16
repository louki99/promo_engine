package com.promo.engine.service.impl;

import com.promo.engine.dto.ConditionDTO;
import com.promo.engine.entity.Condition;
import com.promo.engine.entity.PromotionRule;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.ConditionRepository;
import com.promo.engine.repository.PromotionRuleRepository;
import com.promo.engine.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionServiceImpl implements ConditionService {
    private final ConditionRepository conditionRepository;
    private final PromotionRuleRepository ruleRepository;

    @Override
    @Transactional
    public Condition createCondition(ConditionDTO conditionDTO) {
        PromotionRule rule = ruleRepository.findById(conditionDTO.getRuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
        Condition condition = new Condition();
        condition.setRule(rule);
        condition.setConditionType(conditionDTO.getConditionType());
        condition.setOperator(conditionDTO.getOperator());
        condition.setValue(conditionDTO.getValue());
        condition.setEntityType(conditionDTO.getEntityType());
        condition.setEntityId(conditionDTO.getEntityId());
        return conditionRepository.save(condition);
    }

    @Override
    public Condition getConditionById(Long id) {
        return conditionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Condition not found"));
    }

    @Override
    public List<Condition> getAllConditions() {
        return conditionRepository.findAll();
    }

    @Override
    @Transactional
    public Condition updateCondition(Long id, ConditionDTO conditionDTO) {
        Condition condition = getConditionById(id);
        if (conditionDTO.getRuleId() != null && !conditionDTO.getRuleId().equals(condition.getRule().getId())) {
            PromotionRule rule = ruleRepository.findById(conditionDTO.getRuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
            condition.setRule(rule);
        }
        condition.setConditionType(conditionDTO.getConditionType());
        condition.setOperator(conditionDTO.getOperator());
        condition.setValue(conditionDTO.getValue());
        condition.setEntityType(conditionDTO.getEntityType());
        condition.setEntityId(conditionDTO.getEntityId());
        return conditionRepository.save(condition);
    }

    @Override
    @Transactional
    public void deleteCondition(Long id) {
        Condition condition = getConditionById(id);
        conditionRepository.delete(condition);
    }
}

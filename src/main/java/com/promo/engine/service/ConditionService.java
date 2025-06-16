package com.promo.engine.service;

import com.promo.engine.entity.Condition;
import com.promo.engine.dto.ConditionDTO;
import java.util.List;

public interface ConditionService {
    Condition createCondition(ConditionDTO conditionDTO);
    Condition getConditionById(Long id);
    List<Condition> getAllConditions();
    Condition updateCondition(Long id, ConditionDTO conditionDTO);
    void deleteCondition(Long id);
} 
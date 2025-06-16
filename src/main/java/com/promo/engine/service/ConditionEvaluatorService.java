package com.promo.engine.service;

import com.promo.engine.dto.ApplyPromotionRequest;
import com.promo.engine.domain.PromotionRule;

public interface ConditionEvaluatorService {
    boolean evaluateRule(PromotionRule rule, ApplyPromotionRequest request);
} 
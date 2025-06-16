package com.promo.engine.service;

import com.promo.engine.dto.*;
import com.promo.engine.entity.PromotionRule;
import java.math.BigDecimal;

public interface RewardApplicatorService {
    BigDecimal applyRuleRewards(PromotionRule rule, ApplyPromotionRequest request, ApplyPromotionResponse response);
} 
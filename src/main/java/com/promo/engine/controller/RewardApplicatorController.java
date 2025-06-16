package com.promo.engine.controller;

import com.promo.engine.dto.ApplyPromotionRequest;
import com.promo.engine.dto.ApplyPromotionResponse;
import com.promo.engine.domain.PromotionRule;
import com.promo.engine.service.RewardApplicatorService;
import com.promo.engine.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/reward-applicator")
@RequiredArgsConstructor
public class RewardApplicatorController {
    
    private final RewardApplicatorService rewardApplicatorService;
    private final RuleService ruleService;
    
    @PostMapping("/apply")
    public ResponseEntity<BigDecimal> applyReward(
            @RequestBody ApplyPromotionRequest request,
            @RequestParam Long ruleId) {
        PromotionRule rule = ruleService.getRuleById(ruleId);
        ApplyPromotionResponse response = new ApplyPromotionResponse();
        return ResponseEntity.ok(rewardApplicatorService.applyRuleRewards(rule, request, response));
    }
}
package com.promo.engine.controller;

import com.promo.engine.dto.ApplyPromotionRequest;
import com.promo.engine.entity.PromotionRule;
import com.promo.engine.service.ConditionEvaluatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/condition-evaluator")
@RequiredArgsConstructor
public class ConditionEvaluatorController {
    
    private final ConditionEvaluatorService conditionEvaluatorService;
    
    @PostMapping("/evaluate")
    public ResponseEntity<Boolean> evaluateRule(
            @RequestBody ApplyPromotionRequest request,
            @RequestParam Long ruleId) {
        PromotionRule rule = new PromotionRule(); // TODO: Get rule from service
        rule.setId(ruleId);
        return ResponseEntity.ok(conditionEvaluatorService.evaluateRule(rule, request));
    }
} 
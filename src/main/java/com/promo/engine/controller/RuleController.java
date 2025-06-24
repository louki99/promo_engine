package com.promo.engine.controller;

import com.promo.engine.dto.ConditionDTO;
import com.promo.engine.dto.RuleDTO;
import com.promo.engine.dto.TierDTO;
import com.promo.engine.domain.RuleEntity;
import com.promo.engine.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
public class RuleController {
    
    private final RuleService ruleService;
    
    @PostMapping
    public ResponseEntity<RuleEntity> createRule(@Valid @RequestBody RuleDTO ruleDTO) {
        return ResponseEntity.ok(ruleService.createRule(ruleDTO));
    }
    
    @GetMapping
    public ResponseEntity<List<RuleEntity>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RuleEntity> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }
    
    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<List<RuleEntity>> getRulesByPromotionId(@PathVariable Long promotionId) {
        return ResponseEntity.ok(ruleService.getRulesByPromotionId(promotionId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RuleEntity> updateRule(@PathVariable Long id, @Valid @RequestBody RuleDTO ruleDTO) {
        return ResponseEntity.ok(ruleService.updateRule(id, ruleDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{ruleId}/conditions")
    public ResponseEntity<RuleEntity> addCondition(@PathVariable Long ruleId, @Valid @RequestBody ConditionDTO conditionDTO) {
        return ResponseEntity.ok(ruleService.addCondition(ruleId, conditionDTO));
    }
    
    @DeleteMapping("/{ruleId}/conditions/{conditionId}")
    public ResponseEntity<RuleEntity> removeCondition(@PathVariable Long ruleId, @PathVariable Long conditionId) {
        return ResponseEntity.ok(ruleService.removeCondition(ruleId, conditionId));
    }
    
    @PostMapping("/{ruleId}/tiers")
    public ResponseEntity<RuleEntity> addTier(@PathVariable Long ruleId, @Valid @RequestBody TierDTO tierDTO) {
        return ResponseEntity.ok(ruleService.addTier(ruleId, tierDTO));
    }
    
    @DeleteMapping("/{ruleId}/tiers/{tierId}")
    public ResponseEntity<RuleEntity> removeTier(@PathVariable Long ruleId, @PathVariable Long tierId) {
        return ResponseEntity.ok(ruleService.removeTier(ruleId, tierId));
    }
} 
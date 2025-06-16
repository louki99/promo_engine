package com.promo.engine.controller;

import com.promo.engine.dto.ConditionDTO;
import com.promo.engine.entity.Condition;
import com.promo.engine.service.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conditions")
@RequiredArgsConstructor
public class ConditionController {
    
    private final ConditionService conditionService;
    
    @PostMapping
    public ResponseEntity<Condition> createCondition(@RequestBody ConditionDTO conditionDTO) {
        return ResponseEntity.ok(conditionService.createCondition(conditionDTO));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Condition> getConditionById(@PathVariable Long id) {
        return ResponseEntity.ok(conditionService.getConditionById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Condition>> getAllConditions() {
        return ResponseEntity.ok(conditionService.getAllConditions());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Condition> updateCondition(
            @PathVariable Long id,
            @RequestBody ConditionDTO conditionDTO) {
        return ResponseEntity.ok(conditionService.updateCondition(id, conditionDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCondition(@PathVariable Long id) {
        conditionService.deleteCondition(id);
        return ResponseEntity.ok().build();
    }
} 
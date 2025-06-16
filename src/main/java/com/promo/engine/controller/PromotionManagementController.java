package com.promo.engine.controller;

import com.promo.engine.dto.PromotionDTO;
import com.promo.engine.entity.Promotion;
import com.promo.engine.service.PromotionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotion-management")
@RequiredArgsConstructor
public class PromotionManagementController {
    
    private final PromotionManagementService promotionManagementService;
    
    @PostMapping
    public ResponseEntity<Promotion> createPromotion(@RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionManagementService.createPromotion(promotionDTO));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.getPromotionById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionManagementService.getAllPromotions());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(
            @PathVariable Long id,
            @RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionManagementService.updatePromotion(id, promotionDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionManagementService.deletePromotion(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<Promotion> activatePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.activatePromotion(id));
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Promotion> deactivatePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.deactivatePromotion(id));
    }
} 
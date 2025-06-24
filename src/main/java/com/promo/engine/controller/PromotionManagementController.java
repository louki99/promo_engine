package com.promo.engine.controller;

import com.promo.engine.dto.PromotionDTO;
import com.promo.engine.domain.PromotionEntity;
import com.promo.engine.service.impl.PromotionManagementServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotion-management")
@RequiredArgsConstructor
public class PromotionManagementController {
    
    private final PromotionManagementServiceImpl promotionManagementService;
    
    @PostMapping
    public ResponseEntity<PromotionEntity> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionManagementService.createPromotion(promotionDTO));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PromotionEntity> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.getPromotionById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<PromotionEntity>> getAllPromotions() {
        return ResponseEntity.ok(promotionManagementService.getAllPromotions());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PromotionEntity> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionManagementService.updatePromotion(id, promotionDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionManagementService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<PromotionEntity> activatePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.activatePromotion(id));
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<PromotionEntity> deactivatePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.deactivatePromotion(id));
    }
} 
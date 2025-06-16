package com.promo.engine.controller;

import com.promo.engine.dto.ApplyPromotionRequest;
import com.promo.engine.dto.ApplyPromotionResponse;
import com.promo.engine.dto.PromotionDTO;
import com.promo.engine.domain.Promotion;
import com.promo.engine.service.PromotionEngineService;
import com.promo.engine.service.PromotionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionEngineService promotionEngineService;
    private final PromotionManagementService promotionManagementService;

    @PostMapping("/calculate")
    public ResponseEntity<ApplyPromotionResponse> calculatePromotion(@Valid @RequestBody ApplyPromotionRequest request) {
        return ResponseEntity.ok(promotionEngineService.calculatePromotions(request));
    }

    @PostMapping("/eligible")
    public ResponseEntity<List<PromotionDTO>> getEligiblePromotions(@Valid @RequestBody ApplyPromotionRequest request) {
        return ResponseEntity.ok(promotionEngineService.getEligiblePromotions(request));
    }

    @PostMapping("/validate/{promoCode}")
    public ResponseEntity<Boolean> validatePromoCode(@PathVariable String promoCode, @Valid @RequestBody ApplyPromotionRequest request) {
        return ResponseEntity.ok(promotionEngineService.validatePromotionCode(promoCode, request));
    }

    @PostMapping
    public ResponseEntity<Promotion> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionManagementService.createPromotion(promotionDTO));
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionManagementService.getAllPromotions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.getPromotionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionManagementService.updatePromotion(id, promotionDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionManagementService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Promotion> activatePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.activatePromotion(id));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Promotion> deactivatePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(promotionManagementService.deactivatePromotion(id));
    }
} 
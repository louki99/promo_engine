package com.promo.engine.controller;

import com.promo.engine.dto.BundlePromotionDTO;
import com.promo.engine.domain.BundlePromotion;
import com.promo.engine.service.BundlePromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bundle-promotions")
@RequiredArgsConstructor
public class BundlePromotionController {
    
    private final BundlePromotionService bundlePromotionService;
    
    @PostMapping
    public ResponseEntity<BundlePromotionDTO> createBundlePromotion(
            @Valid @RequestBody BundlePromotionDTO bundlePromotionDTO) {
        return ResponseEntity.ok(bundlePromotionService.createBundlePromotion(bundlePromotionDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BundlePromotionDTO> updateBundlePromotion(
            @PathVariable Long id,
            @Valid @RequestBody BundlePromotionDTO bundlePromotionDTO) {
        return ResponseEntity.ok(bundlePromotionService.updateBundlePromotion(id, bundlePromotionDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBundlePromotion(@PathVariable Long id) {
        bundlePromotionService.deleteBundlePromotion(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BundlePromotionDTO> getBundlePromotion(@PathVariable Long id) {
        return ResponseEntity.ok(bundlePromotionService.getBundlePromotion(id));
    }
    
    @GetMapping
    public ResponseEntity<List<BundlePromotionDTO>> getAllBundlePromotions() {
        return ResponseEntity.ok(bundlePromotionService.getAllBundlePromotions());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<BundlePromotionDTO>> getActiveBundlePromotions() {
        return ResponseEntity.ok(bundlePromotionService.getActiveBundlePromotions());
    }
    
    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<List<BundlePromotionDTO>> getBundlePromotionsByPromotionId(
            @PathVariable Long promotionId) {
        return ResponseEntity.ok(bundlePromotionService.getBundlePromotionsByPromotionId(promotionId));
    }
    
    @PostMapping("/{id}/check-complete")
    public ResponseEntity<Boolean> isBundleComplete(
            @PathVariable Long id,
            @RequestBody List<BundlePromotion.BundleItem> cartItems) {
        return ResponseEntity.ok(bundlePromotionService.isBundleComplete(id, cartItems));
    }
    
    @PostMapping("/{id}/calculate-discount")
    public ResponseEntity<BundlePromotionDTO> calculateBundleDiscount(
            @PathVariable Long id,
            @RequestBody List<BundlePromotion.BundleItem> cartItems) {
        return ResponseEntity.ok(bundlePromotionService.calculateBundleDiscount(id, cartItems));
    }
} 
package com.promo.engine.controller;

import com.promo.engine.service.DynamicPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dynamic-pricing")
@RequiredArgsConstructor
public class DynamicPricingController {
    
    private final DynamicPricingService dynamicPricingService;
    
    @GetMapping("/price")
    public ResponseEntity<BigDecimal> calculateDynamicPrice(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam Long customerId) {
        return ResponseEntity.ok(dynamicPricingService.calculateDynamicPrice(productId, quantity, customerId));
    }
    
    @PutMapping("/products/{productId}/price")
    public ResponseEntity<Void> updateProductPrice(
            @PathVariable Long productId,
            @RequestParam BigDecimal newPrice) {
        dynamicPricingService.updateProductPrice(productId, newPrice);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/products/{productId}/inventory")
    public ResponseEntity<Void> updateInventoryLevel(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        dynamicPricingService.updateInventoryLevel(productId, quantity);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/products/{productId}/demand-factor")
    public ResponseEntity<Void> updateDemandFactor(
            @PathVariable Long productId,
            @RequestParam double factor) {
        dynamicPricingService.updateDemandFactor(productId, factor);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/products/prices")
    public ResponseEntity<Map<Long, BigDecimal>> getCurrentPrices(@RequestBody List<Long> productIds) {
        return ResponseEntity.ok(dynamicPricingService.getCurrentPrices(productIds));
    }
    
    @GetMapping("/products/inventory")
    public ResponseEntity<Map<Long, Integer>> getInventoryLevels(@RequestBody List<Long> productIds) {
        return ResponseEntity.ok(dynamicPricingService.getInventoryLevels(productIds));
    }
    
    @GetMapping("/products/demand-factors")
    public ResponseEntity<Map<Long, Double>> getDemandFactors(@RequestBody List<Long> productIds) {
        return ResponseEntity.ok(dynamicPricingService.getDemandFactors(productIds));
    }
    
    @PostMapping("/products/{productId}/seasonal")
    public ResponseEntity<Void> applySeasonalMultiplier(
            @PathVariable Long productId,
            @RequestParam double multiplier) {
        dynamicPricingService.applySeasonalMultiplier(productId, multiplier);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/products/{productId}/time-discount")
    public ResponseEntity<Void> applyTimeBasedDiscount(
            @PathVariable Long productId,
            @RequestParam BigDecimal discount) {
        dynamicPricingService.applyTimeBasedDiscount(productId, discount);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/products/{productId}/segment-discount")
    public ResponseEntity<Void> applyCustomerSegmentDiscount(
            @PathVariable Long productId,
            @RequestParam Long segmentId,
            @RequestParam BigDecimal discount) {
        dynamicPricingService.applyCustomerSegmentDiscount(productId, segmentId, discount);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/products/{productId}/bulk-discount")
    public ResponseEntity<Void> applyBulkDiscount(
            @PathVariable Long productId,
            @RequestParam int minQuantity,
            @RequestParam BigDecimal discount) {
        dynamicPricingService.applyBulkDiscount(productId, minQuantity, discount);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/products/{productId}/reset")
    public ResponseEntity<Void> resetDynamicPricing(@PathVariable Long productId) {
        dynamicPricingService.resetDynamicPricing(productId);
        return ResponseEntity.ok().build();
    }
} 
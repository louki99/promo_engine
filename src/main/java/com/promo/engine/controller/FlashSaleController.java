package com.promo.engine.controller;

import com.promo.engine.domain.FlashSale;
import com.promo.engine.domain.FlashSaleInventory;
import com.promo.engine.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/flash-sales")
@RequiredArgsConstructor
public class FlashSaleController {
    
    private final FlashSaleService flashSaleService;
    
    @PostMapping
    public ResponseEntity<FlashSale> createFlashSale(@RequestBody FlashSale flashSale) {
        return ResponseEntity.ok(flashSaleService.createFlashSale(flashSale));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFlashSale(@PathVariable Long id, @RequestBody FlashSale flashSale) {
        flashSale.setId(id);
        flashSaleService.updateFlashSale(flashSale);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashSale(@PathVariable Long id) {
        flashSaleService.deleteFlashSale(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FlashSale> getFlashSale(@PathVariable Long id) {
        return ResponseEntity.ok(flashSaleService.getFlashSale(id));
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<FlashSale>> getActiveFlashSales() {
        return ResponseEntity.ok(flashSaleService.getActiveFlashSales());
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<FlashSale>> getUpcomingFlashSales() {
        return ResponseEntity.ok(flashSaleService.getUpcomingFlashSales());
    }
    
    @PostMapping("/{flashSaleId}/inventory/reserve")
    public ResponseEntity<Void> reserveInventory(
            @PathVariable Long flashSaleId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        flashSaleService.reserveInventory(flashSaleId, productId, quantity);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{flashSaleId}/inventory/release")
    public ResponseEntity<Void> releaseInventory(
            @PathVariable Long flashSaleId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        flashSaleService.releaseInventory(flashSaleId, productId, quantity);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{flashSaleId}/inventory/{productId}")
    public ResponseEntity<Integer> getAvailableInventory(
            @PathVariable Long flashSaleId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(flashSaleService.getAvailableInventory(flashSaleId, productId));
    }
    
    @GetMapping("/{flashSaleId}/inventory")
    public ResponseEntity<Map<Long, Integer>> getAvailableInventoryForProducts(
            @PathVariable Long flashSaleId,
            @RequestBody List<Long> productIds) {
        return ResponseEntity.ok(flashSaleService.getAvailableInventoryForProducts(flashSaleId, productIds));
    }
    
    @GetMapping("/{flashSaleId}/price")
    public ResponseEntity<BigDecimal> calculateFlashSalePrice(
            @PathVariable Long flashSaleId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(flashSaleService.calculateFlashSalePrice(flashSaleId, productId, quantity));
    }
    
    @PutMapping("/{flashSaleId}/demand-factor")
    public ResponseEntity<Void> updateDemandFactor(
            @PathVariable Long flashSaleId,
            @RequestParam Long productId,
            @RequestParam double factor) {
        flashSaleService.updateDemandFactor(flashSaleId, productId, factor);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{flashSaleId}/early-bird")
    public ResponseEntity<Void> applyEarlyBirdDiscount(
            @PathVariable Long flashSaleId,
            @RequestParam BigDecimal discount) {
        flashSaleService.applyEarlyBirdDiscount(flashSaleId, discount);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{flashSaleId}/loyalty")
    public ResponseEntity<Void> applyLoyaltyMultiplier(
            @PathVariable Long flashSaleId,
            @RequestParam Long customerId,
            @RequestParam double multiplier) {
        flashSaleService.applyLoyaltyMultiplier(flashSaleId, customerId, multiplier);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{flashSaleId}/bulk")
    public ResponseEntity<Void> applyBulkPurchaseDiscount(
            @PathVariable Long flashSaleId,
            @RequestParam int minQuantity,
            @RequestParam BigDecimal discount) {
        flashSaleService.applyBulkPurchaseDiscount(flashSaleId, minQuantity, discount);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{flashSaleId}/inventory")
    public ResponseEntity<Void> updateInventoryLevels(
            @PathVariable Long flashSaleId,
            @RequestBody Map<Long, Integer> inventoryUpdates) {
        flashSaleService.updateInventoryLevels(flashSaleId, inventoryUpdates);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{flashSaleId}/start")
    public ResponseEntity<Void> startFlashSale(@PathVariable Long flashSaleId) {
        flashSaleService.startFlashSale(flashSaleId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{flashSaleId}/end")
    public ResponseEntity<Void> endFlashSale(@PathVariable Long flashSaleId) {
        flashSaleService.endFlashSale(flashSaleId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{flashSaleId}/status")
    public ResponseEntity<Boolean> isFlashSaleActive(@PathVariable Long flashSaleId) {
        return ResponseEntity.ok(flashSaleService.isFlashSaleActive(flashSaleId));
    }
    
    @PostMapping("/{flashSaleId}/extend")
    public ResponseEntity<Void> extendFlashSale(
            @PathVariable Long flashSaleId,
            @RequestParam int additionalMinutes) {
        flashSaleService.extendFlashSale(flashSaleId, additionalMinutes);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{flashSaleId}/pause")
    public ResponseEntity<Void> pauseFlashSale(@PathVariable Long flashSaleId) {
        flashSaleService.pauseFlashSale(flashSaleId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{flashSaleId}/resume")
    public ResponseEntity<Void> resumeFlashSale(@PathVariable Long flashSaleId) {
        flashSaleService.resumeFlashSale(flashSaleId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{flashSaleId}/inventory/all")
    public ResponseEntity<Map<Long, FlashSaleInventory>> getFlashSaleInventory(@PathVariable Long flashSaleId) {
        return ResponseEntity.ok(flashSaleService.getFlashSaleInventory(flashSaleId));
    }
    
    @PutMapping("/{flashSaleId}/status")
    public ResponseEntity<Void> updateFlashSaleStatus(
            @PathVariable Long flashSaleId,
            @RequestParam FlashSale.Status status) {
        flashSaleService.updateFlashSaleStatus(flashSaleId, status);
        return ResponseEntity.ok().build();
    }
} 
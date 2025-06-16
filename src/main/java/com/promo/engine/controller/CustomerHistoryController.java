package com.promo.engine.controller;

import com.promo.engine.dto.CustomerHistoryUpdateRequest;
import com.promo.engine.domain.CustomerHistory;
import com.promo.engine.domain.CustomerSegment;
import com.promo.engine.service.CustomerHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer-history")
@RequiredArgsConstructor
public class CustomerHistoryController {
    
    private final CustomerHistoryService customerHistoryService;
    
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerHistory> getCustomerHistory(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerHistoryService.getCustomerHistory(customerId));
    }
    
    @PostMapping("/{customerId}/update")
    public ResponseEntity<Void> updateCustomerHistory(
            @PathVariable Long customerId,
            @RequestBody CustomerHistoryUpdateRequest request) {
        customerHistoryService.updateCustomerHistory(
            customerId,
            request.getOrderTotal(),
            request.getPurchases()
        );
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{customerId}/segments")
    public ResponseEntity<List<CustomerSegment>> getCustomerSegments(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerHistoryService.getCustomerSegments(customerId));
    }
    
    @PostMapping("/{customerId}/segments/update")
    public ResponseEntity<Void> updateCustomerSegments(@PathVariable Long customerId) {
        customerHistoryService.updateCustomerSegments(customerId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{customerId}/category-spend")
    public ResponseEntity<Map<Long, BigDecimal>> getCategorySpend(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerHistoryService.getCategorySpend(customerId));
    }
    
    @GetMapping("/{customerId}/total-spend")
    public ResponseEntity<BigDecimal> getTotalSpend(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerHistoryService.getTotalSpend(customerId));
    }
    
    @GetMapping("/{customerId}/loyalty-points")
    public ResponseEntity<Integer> getLoyaltyPoints(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerHistoryService.getLoyaltyPoints(customerId));
    }
    
    @GetMapping("/{customerId}/loyalty-tier")
    public ResponseEntity<String> getLoyaltyTier(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerHistoryService.getLoyaltyTier(customerId));
    }
    
    @PostMapping("/{customerId}/loyalty-points")
    public ResponseEntity<Void> updateLoyaltyPoints(
            @PathVariable Long customerId,
            @RequestParam int points) {
        customerHistoryService.updateLoyaltyPoints(customerId, points);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{customerId}/recent-purchases")
    public ResponseEntity<List<CustomerHistory.ProductPurchase>> getRecentPurchases(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(customerHistoryService.getRecentPurchases(customerId, limit));
    }
    
    @GetMapping("/{customerId}/promotion-eligibility/{promotionId}")
    public ResponseEntity<Boolean> isEligibleForPromotion(
            @PathVariable Long customerId,
            @PathVariable Long promotionId) {
        return ResponseEntity.ok(customerHistoryService.isEligibleForPromotion(customerId, promotionId));
    }
    
    @PostMapping("/{customerId}/recalculate")
    public ResponseEntity<Void> recalculateCustomerMetrics(@PathVariable Long customerId) {
        customerHistoryService.recalculateCustomerMetrics(customerId);
        return ResponseEntity.ok().build();
    }
} 
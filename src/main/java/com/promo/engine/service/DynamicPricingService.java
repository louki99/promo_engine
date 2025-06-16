package com.promo.engine.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DynamicPricingService {
    BigDecimal calculateDynamicPrice(Long productId, int quantity, Long customerId);
    
    void updateProductPrice(Long productId, BigDecimal newPrice);
    
    void updateInventoryLevel(Long productId, int quantity);
    
    void updateDemandFactor(Long productId, double factor);
    
    Map<Long, BigDecimal> getCurrentPrices(List<Long> productIds);
    
    Map<Long, Integer> getInventoryLevels(List<Long> productIds);
    
    Map<Long, Double> getDemandFactors(List<Long> productIds);
    
    void applySeasonalMultiplier(Long productId, double multiplier);
    
    void applyTimeBasedDiscount(Long productId, BigDecimal discount);
    
    void applyCustomerSegmentDiscount(Long productId, Long segmentId, BigDecimal discount);
    
    void applyBulkDiscount(Long productId, int minQuantity, BigDecimal discount);
    
    void resetDynamicPricing(Long productId);
} 
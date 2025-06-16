package com.promo.engine.service;

import com.promo.engine.entity.CustomerHistory;
import com.promo.engine.entity.CustomerSegment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustomerHistoryService {
    CustomerHistory getCustomerHistory(Long customerId);
    
    void updateCustomerHistory(Long customerId, BigDecimal orderTotal, List<CustomerHistory.ProductPurchase> purchases);
    
    List<CustomerSegment> getCustomerSegments(Long customerId);
    
    void updateCustomerSegments(Long customerId);
    
    Map<Long, BigDecimal> getCategorySpend(Long customerId);
    
    BigDecimal getTotalSpend(Long customerId);
    
    Integer getLoyaltyPoints(Long customerId);
    
    String getLoyaltyTier(Long customerId);
    
    void updateLoyaltyPoints(Long customerId, int points);
    
    List<CustomerHistory.ProductPurchase> getRecentPurchases(Long customerId, int limit);
    
    boolean isEligibleForPromotion(Long customerId, Long promotionId);
    
    void recalculateCustomerMetrics(Long customerId);
} 
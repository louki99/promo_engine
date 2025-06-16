package com.promo.engine.service.impl;

import com.promo.engine.entity.CustomerHistory;
import com.promo.engine.entity.CustomerSegment;
import com.promo.engine.entity.TimeBasedCondition;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.CustomerHistoryRepository;
import com.promo.engine.repository.CustomerSegmentRepository;
import com.promo.engine.repository.TimeBasedConditionRepository;
import com.promo.engine.service.CustomerHistoryService;
import com.promo.engine.service.DynamicPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DynamicPricingServiceImpl implements DynamicPricingService {
    
    private final CustomerHistoryService customerHistoryService;
    private final CustomerSegmentRepository segmentRepository;
    private final TimeBasedConditionRepository timeBasedConditionRepository;
    
    // In-memory cache for dynamic pricing data
    private final Map<Long, BigDecimal> basePrices = new ConcurrentHashMap<>();
    private final Map<Long, Integer> inventoryLevels = new ConcurrentHashMap<>();
    private final Map<Long, Double> demandFactors = new ConcurrentHashMap<>();
    private final Map<Long, Double> seasonalMultipliers = new ConcurrentHashMap<>();
    private final Map<Long, BigDecimal> timeBasedDiscounts = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, BigDecimal>> segmentDiscounts = new ConcurrentHashMap<>();
    private final Map<Long, Map<Integer, BigDecimal>> bulkDiscounts = new ConcurrentHashMap<>();
    
    @Override
    public BigDecimal calculateDynamicPrice(Long productId, int quantity, Long customerId) {
        BigDecimal basePrice = basePrices.getOrDefault(productId, BigDecimal.ZERO);
        if (basePrice.equals(BigDecimal.ZERO)) {
            throw new ResourceNotFoundException("Product price not found");
        }
        
        // Apply inventory-based adjustment
        double inventoryFactor = calculateInventoryFactor(productId);
        
        // Apply demand-based adjustment
        double demandFactor = demandFactors.getOrDefault(productId, 1.0);
        
        // Apply seasonal multiplier
        double seasonalMultiplier = seasonalMultipliers.getOrDefault(productId, 1.0);
        
        // Apply time-based discount if applicable
        BigDecimal timeDiscount = timeBasedDiscounts.getOrDefault(productId, BigDecimal.ZERO);
        
        // Apply customer segment discount if applicable
        BigDecimal segmentDiscount = calculateSegmentDiscount(productId, customerId);
        
        // Apply bulk discount if applicable
        BigDecimal bulkDiscount = calculateBulkDiscount(productId, quantity);
        
        // Calculate final price
        BigDecimal adjustedPrice = basePrice
                .multiply(BigDecimal.valueOf(inventoryFactor))
                .multiply(BigDecimal.valueOf(demandFactor))
                .multiply(BigDecimal.valueOf(seasonalMultiplier))
                .subtract(timeDiscount)
                .subtract(segmentDiscount)
                .subtract(bulkDiscount);
        
        // Ensure price doesn't go below minimum threshold
        return adjustedPrice.max(BigDecimal.valueOf(0.01));
    }
    
    @Override
    @Transactional
    public void updateProductPrice(Long productId, BigDecimal newPrice) {
        basePrices.put(productId, newPrice);
    }
    
    @Override
    @Transactional
    public void updateInventoryLevel(Long productId, int quantity) {
        inventoryLevels.put(productId, quantity);
    }
    
    @Override
    @Transactional
    public void updateDemandFactor(Long productId, double factor) {
        demandFactors.put(productId, factor);
    }
    
    @Override
    public Map<Long, BigDecimal> getCurrentPrices(List<Long> productIds) {
        Map<Long, BigDecimal> prices = new HashMap<>();
        for (Long productId : productIds) {
            prices.put(productId, basePrices.getOrDefault(productId, BigDecimal.ZERO));
        }
        return prices;
    }
    
    @Override
    public Map<Long, Integer> getInventoryLevels(List<Long> productIds) {
        Map<Long, Integer> levels = new HashMap<>();
        for (Long productId : productIds) {
            levels.put(productId, inventoryLevels.getOrDefault(productId, 0));
        }
        return levels;
    }
    
    @Override
    public Map<Long, Double> getDemandFactors(List<Long> productIds) {
        Map<Long, Double> factors = new HashMap<>();
        for (Long productId : productIds) {
            factors.put(productId, demandFactors.getOrDefault(productId, 1.0));
        }
        return factors;
    }
    
    @Override
    @Transactional
    public void applySeasonalMultiplier(Long productId, double multiplier) {
        seasonalMultipliers.put(productId, multiplier);
    }
    
    @Override
    @Transactional
    public void applyTimeBasedDiscount(Long productId, BigDecimal discount) {
        timeBasedDiscounts.put(productId, discount);
    }
    
    @Override
    @Transactional
    public void applyCustomerSegmentDiscount(Long productId, Long segmentId, BigDecimal discount) {
        segmentDiscounts.computeIfAbsent(productId, k -> new HashMap<>())
                .put(segmentId, discount);
    }
    
    @Override
    @Transactional
    public void applyBulkDiscount(Long productId, int minQuantity, BigDecimal discount) {
        bulkDiscounts.computeIfAbsent(productId, k -> new HashMap<>())
                .put(minQuantity, discount);
    }
    
    @Override
    @Transactional
    public void resetDynamicPricing(Long productId) {
        basePrices.remove(productId);
        inventoryLevels.remove(productId);
        demandFactors.remove(productId);
        seasonalMultipliers.remove(productId);
        timeBasedDiscounts.remove(productId);
        segmentDiscounts.remove(productId);
        bulkDiscounts.remove(productId);
    }
    
    private double calculateInventoryFactor(Long productId) {
        int inventory = inventoryLevels.getOrDefault(productId, 0);
        if (inventory <= 0) {
            return 1.2; // Increase price when out of stock
        } else if (inventory < 10) {
            return 1.1; // Slight increase for low inventory
        } else if (inventory > 100) {
            return 0.9; // Discount for high inventory
        }
        return 1.0; // Normal price for balanced inventory
    }
    
    private BigDecimal calculateSegmentDiscount(Long productId, Long customerId) {
        List<CustomerSegment> segments = customerHistoryService.getCustomerSegments(customerId);
        Map<Long, BigDecimal> productSegmentDiscounts = segmentDiscounts.getOrDefault(productId, Collections.emptyMap());
        
        return segments.stream()
                .map(segment -> productSegmentDiscounts.getOrDefault(segment.getId(), BigDecimal.ZERO))
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
    
    private BigDecimal calculateBulkDiscount(Long productId, int quantity) {
        Map<Integer, BigDecimal> productBulkDiscounts = bulkDiscounts.getOrDefault(productId, Collections.emptyMap());
        
        return productBulkDiscounts.entrySet().stream()
                .filter(entry -> quantity >= entry.getKey())
                .map(Map.Entry::getValue)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
} 
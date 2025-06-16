package com.promo.engine.service.impl;

import com.promo.engine.domain.CustomerSegment;
import com.promo.engine.domain.PromoCustomer;
import com.promo.engine.domain.PromoProduct;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.CustomerSegmentRepository;
import com.promo.engine.repository.PromoCustomerRepository;
import com.promo.engine.repository.PromoProductRepository;
import com.promo.engine.repository.TimeBasedConditionRepository;
import com.promo.engine.service.CustomerHistoryService;
import com.promo.engine.service.DynamicPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DynamicPricingServiceImpl implements DynamicPricingService {
    private final PromoProductRepository promoProductRepository;
    private final PromoCustomerRepository promoCustomerRepository;

    // Segment/bulk/seasonal/time/demand discounts can remain in-memory or be persisted as needed
    private final Map<Long, Map<Long, BigDecimal>> segmentDiscounts = new HashMap<>();
    private final Map<Long, Map<Integer, BigDecimal>> bulkDiscounts = new HashMap<>();
    private final Map<Long, Double> seasonalMultipliers = new HashMap<>();
    private final Map<Long, BigDecimal> timeBasedDiscounts = new HashMap<>();
    private final Map<Long, Double> demandFactors = new HashMap<>();

    @Override
    public BigDecimal calculateDynamicPrice(Long productId, int quantity, Long customerId) {
        PromoProduct product = promoProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        BigDecimal basePrice = product.getPrice();
        if (basePrice == null || basePrice.equals(BigDecimal.ZERO)) {
            throw new ResourceNotFoundException("Product price not found");
        }
        int inventory = product.getSkuPoints() != null ? product.getSkuPoints().intValue() : 100;
        double inventoryFactor = calculateInventoryFactor(inventory);
        double demandFactor = demandFactors.getOrDefault(productId, 1.0);
        double seasonalMultiplier = seasonalMultipliers.getOrDefault(productId, 1.0);
        BigDecimal timeDiscount = timeBasedDiscounts.getOrDefault(productId, BigDecimal.ZERO);
        BigDecimal segmentDiscount = calculateSegmentDiscount(productId, customerId);
        BigDecimal bulkDiscount = calculateBulkDiscount(productId, quantity);
        BigDecimal adjustedPrice = basePrice
                .multiply(BigDecimal.valueOf(inventoryFactor))
                .multiply(BigDecimal.valueOf(demandFactor))
                .multiply(BigDecimal.valueOf(seasonalMultiplier))
                .subtract(timeDiscount)
                .subtract(segmentDiscount)
                .subtract(bulkDiscount);
        return adjustedPrice.max(BigDecimal.valueOf(0.01));
    }

    @Override
    @Transactional
    public void updateProductPrice(Long productId, BigDecimal newPrice) {
        PromoProduct product = promoProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setPrice(newPrice);
        promoProductRepository.save(product);
    }

    @Override
    @Transactional
    public void updateInventoryLevel(Long productId, int quantity) {
        PromoProduct product = promoProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setSkuPoints(BigDecimal.valueOf(quantity));
        promoProductRepository.save(product);
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
            PromoProduct product = promoProductRepository.findById(productId).orElse(null);
            prices.put(productId, product != null ? product.getPrice() : BigDecimal.ZERO);
        }
        return prices;
    }

    @Override
    public Map<Long, Integer> getInventoryLevels(List<Long> productIds) {
        Map<Long, Integer> levels = new HashMap<>();
        for (Long productId : productIds) {
            PromoProduct product = promoProductRepository.findById(productId).orElse(null);
            levels.put(productId, product != null && product.getSkuPoints() != null ? product.getSkuPoints().intValue() : 0);
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
        seasonalMultipliers.remove(productId);
        timeBasedDiscounts.remove(productId);
        demandFactors.remove(productId);
        segmentDiscounts.remove(productId);
        bulkDiscounts.remove(productId);
    }

    private double calculateInventoryFactor(int inventory) {
        if (inventory <= 0) {
            return 1.2;
        } else if (inventory < 10) {
            return 1.1;
        } else if (inventory > 100) {
            return 0.9;
        }
        return 1.0;
    }

    private BigDecimal calculateSegmentDiscount(Long productId, Long customerId) {
        PromoCustomer customer = promoCustomerRepository.findById(customerId).orElse(null);
        if (customer == null || customer.getSegmentIds() == null) return BigDecimal.ZERO;
        Map<Long, BigDecimal> productSegmentDiscounts = segmentDiscounts.getOrDefault(productId, Collections.emptyMap());
        return customer.getSegmentIds().stream()
                .map(segmentId -> productSegmentDiscounts.getOrDefault(segmentId, BigDecimal.ZERO))
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
package com.promo.engine.service.impl;

import com.promo.engine.domain.PromoCustomer;
import com.promo.engine.domain.PromoProduct;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.PromoCustomerRepository;
import com.promo.engine.repository.PromoProductRepository;
import com.promo.engine.service.DynamicPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DynamicPricingServiceImpl implements DynamicPricingService {
    private final PromoProductRepository promoProductRepository;
    private final PromoCustomerRepository promoCustomerRepository;
    private final RedisTemplate<String, Object> redisTemplate;

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
        double demandFactor = (Double) redisTemplate.opsForHash().get("demandFactors", productId.toString());
        double seasonalMultiplier = (Double) redisTemplate.opsForHash().get("seasonalMultipliers", productId.toString());
        BigDecimal timeDiscount = (BigDecimal) redisTemplate.opsForHash().get("timeBasedDiscounts", productId.toString());
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
        redisTemplate.opsForHash().put("demandFactors", productId.toString(), factor);
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
            factors.put(productId, (Double) redisTemplate.opsForHash().get("demandFactors", productId.toString()));
        }
        return factors;
    }

    @Override
    @Transactional
    public void applySeasonalMultiplier(Long productId, double multiplier) {
        redisTemplate.opsForHash().put("seasonalMultipliers", productId.toString(), multiplier);
    }

    @Override
    @Transactional
    public void applyTimeBasedDiscount(Long productId, BigDecimal discount) {
        redisTemplate.opsForHash().put("timeBasedDiscounts", productId.toString(), discount);
    }

    @Override
    @Transactional
    public void applyCustomerSegmentDiscount(Long productId, Long segmentId, BigDecimal discount) {
        redisTemplate.opsForHash().put("segmentDiscounts:" + productId, segmentId.toString(), discount);
    }

    @Override
    @Transactional
    public void applyBulkDiscount(Long productId, int minQuantity, BigDecimal discount) {
        redisTemplate.opsForHash().put("bulkDiscounts:" + productId, minQuantity, discount);
    }

    @Override
    @Transactional
    public void resetDynamicPricing(Long productId) {
        redisTemplate.opsForHash().delete("seasonalMultipliers", productId.toString());
        redisTemplate.opsForHash().delete("timeBasedDiscounts", productId.toString());
        redisTemplate.opsForHash().delete("demandFactors", productId.toString());
        redisTemplate.opsForHash().delete("segmentDiscounts:" + productId);
        redisTemplate.opsForHash().delete("bulkDiscounts:" + productId);
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
        Map<Object, Object> productSegmentDiscounts = redisTemplate.opsForHash().entries("segmentDiscounts:" + productId);
        return customer.getSegmentIds().stream()
                .map(segmentId -> (BigDecimal) productSegmentDiscounts.getOrDefault(segmentId.toString(), BigDecimal.ZERO))
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateBulkDiscount(Long productId, int quantity) {
        Map<Object, Object> productBulkDiscounts = redisTemplate.opsForHash().entries("bulkDiscounts:" + productId);
        return productBulkDiscounts.entrySet().stream()
                .filter(entry -> quantity >= (Integer) entry.getKey())
                .map(entry -> (BigDecimal) entry.getValue())
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
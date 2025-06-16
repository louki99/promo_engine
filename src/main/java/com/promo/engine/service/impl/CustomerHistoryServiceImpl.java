package com.promo.engine.service.impl;

import com.promo.engine.entity.CustomerHistory;
import com.promo.engine.entity.CustomerSegment;
import com.promo.engine.entity.Promotion;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.CustomerHistoryRepository;
import com.promo.engine.repository.CustomerSegmentRepository;
import com.promo.engine.repository.PromotionRepository;
import com.promo.engine.service.CustomerHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerHistoryServiceImpl implements CustomerHistoryService {
    
    private final CustomerHistoryRepository customerHistoryRepository;
    private final CustomerSegmentRepository segmentRepository;
    private final PromotionRepository promotionRepository;
    
    @Override
    public CustomerHistory getCustomerHistory(Long customerId) {
        return customerHistoryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer history not found"));
    }
    
    @Override
    @Transactional
    public void updateCustomerHistory(Long customerId, BigDecimal orderTotal, List<CustomerHistory.ProductPurchase> purchases) {
        CustomerHistory history = customerHistoryRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    CustomerHistory newHistory = new CustomerHistory();
                    newHistory.setCustomerId(customerId);
                    newHistory.setTotalSpend(BigDecimal.ZERO);
                    newHistory.setTotalOrders(0);
                    newHistory.setCategorySpend(new HashMap<>());
                    newHistory.setProductHistory(new ArrayList<>());
                    newHistory.setLoyaltyPoints(0);
                    return newHistory;
                });
        
        history.updateAfterOrder(orderTotal, purchases);
        customerHistoryRepository.save(history);
        
        // Update segments after history update
        updateCustomerSegments(customerId);
    }
    
    @Override
    public List<CustomerSegment> getCustomerSegments(Long customerId) {
        CustomerHistory history = getCustomerHistory(customerId);
        return segmentRepository.findAll().stream()
                .filter(segment -> segment.isCustomerEligible(customerId, history.getTotalSpend(), history.getTotalOrders()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void updateCustomerSegments(Long customerId) {
        CustomerHistory history = getCustomerHistory(customerId);
        List<CustomerSegment> eligibleSegments = getCustomerSegments(customerId);
        
        // Update segment membership logic here if needed
        history.setLastSegmentUpdate(LocalDateTime.now());
        customerHistoryRepository.save(history);
    }
    
    @Override
    public Map<Long, BigDecimal> getCategorySpend(Long customerId) {
        return getCustomerHistory(customerId).getCategorySpend();
    }
    
    @Override
    public BigDecimal getTotalSpend(Long customerId) {
        return getCustomerHistory(customerId).getTotalSpend();
    }
    
    @Override
    public Integer getLoyaltyPoints(Long customerId) {
        return getCustomerHistory(customerId).getLoyaltyPoints();
    }
    
    @Override
    public String getLoyaltyTier(Long customerId) {
        return getCustomerHistory(customerId).getLoyaltyTier();
    }
    
    @Override
    @Transactional
    public void updateLoyaltyPoints(Long customerId, int points) {
        CustomerHistory history = getCustomerHistory(customerId);
        history.updateLoyaltyPoints(points);
        customerHistoryRepository.save(history);
    }
    
    @Override
    public List<CustomerHistory.ProductPurchase> getRecentPurchases(Long customerId, int limit) {
        CustomerHistory history = getCustomerHistory(customerId);
        return history.getProductHistory().stream()
                .sorted(Comparator.comparing(CustomerHistory.ProductPurchase::getPurchaseDate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isEligibleForPromotion(Long customerId, Long promotionId) {
        CustomerHistory history = getCustomerHistory(customerId);
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        
        // Check if customer meets promotion criteria
        // This is a simplified check - you would need to implement more complex logic
        // based on your specific promotion rules
        return true; // Placeholder
    }
    
    @Override
    @Transactional
    public void recalculateCustomerMetrics(Long customerId) {
        CustomerHistory history = getCustomerHistory(customerId);
        
        // Recalculate total spend
        BigDecimal totalSpend = history.getProductHistory().stream()
                .map(CustomerHistory.ProductPurchase::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Recalculate category spend
        Map<Long, BigDecimal> categorySpend = history.getProductHistory().stream()
                .filter(purchase -> purchase.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        CustomerHistory.ProductPurchase::getCategoryId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                CustomerHistory.ProductPurchase::getTotalPrice,
                                BigDecimal::add
                        )
                ));
        
        // Update history
        history.setTotalSpend(totalSpend);
        history.setCategorySpend(categorySpend);
        history.setTotalOrders(history.getProductHistory().size());
        history.setAverageOrderValue(
                history.getTotalOrders() > 0
                        ? totalSpend.divide(BigDecimal.valueOf(history.getTotalOrders()), 2, BigDecimal.ROUND_HALF_UP)
                        : BigDecimal.ZERO
        );
        
        customerHistoryRepository.save(history);
    }
} 
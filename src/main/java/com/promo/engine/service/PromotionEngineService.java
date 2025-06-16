package com.promo.engine.service;

import com.promo.engine.dto.*;
import com.promo.engine.domain.Promotion;
import com.promo.engine.domain.PromotionCustomerUsage;
import com.promo.engine.domain.PromotionRule;
import com.promo.engine.repository.PromotionCustomerUsageRepository;
import com.promo.engine.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionEngineService {
    
    private final PromotionRepository promotionRepository;
    private final PromotionCustomerUsageRepository usageRepository;
    private final ConditionEvaluatorService conditionEvaluator;
    private final RewardApplicatorService rewardApplicator;
    
    @Transactional
    public ApplyPromotionResponse calculatePromotions(ApplyPromotionRequest request) {
        log.info("Calculating promotions for customer: {}", request.getCustomerId());
        
        // Initialize response
        ApplyPromotionResponse response = new ApplyPromotionResponse();
        response.setLineItems(initializeLineItems(request.getCartItems()));
        response.setFreeItems(new ArrayList<>());
        response.setAppliedPromotions(new ArrayList<>());
        
        // Calculate original total
        BigDecimal originalTotal = calculateOriginalTotal(request.getCartItems());
        response.setOriginalTotal(originalTotal);
        
        // Get active promotions
        List<Promotion> activePromotions = promotionRepository.findActivePromotions(LocalDateTime.now());
        
        // Sort by priority (highest first)
        activePromotions.sort((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()));
        
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        // Separate exclusive and stackable promotions
        List<Promotion> exclusivePromos = activePromotions.stream()
            .filter(Promotion::isExclusive)
            .collect(Collectors.toList());
            
        if (!exclusivePromos.isEmpty()) {
            // Apply only the highest priority exclusive promotion
            Promotion best = exclusivePromos.get(0); // already sorted
            if (isPromotionApplicable(best, request)) {
                ApplyPromotionResult result = applyPromotion(best, request, response);
                if (result.isApplied()) {
                    totalDiscount = totalDiscount.add(result.getDiscountAmount());
                    recordPromotionUsage(best, request.getCustomerId());
                    AppliedPromotion appliedPromo = new AppliedPromotion();
                    appliedPromo.setPromoCode(best.getPromoCode());
                    appliedPromo.setDescription(best.getDescription());
                    response.getAppliedPromotions().add(appliedPromo);
                }
            }
        } else {
            // Stackable logic: group by stackingGroup, apply with limits
            Map<String, List<Promotion>> byGroup = activePromotions.stream()
                .filter(p -> !p.isExclusive())
                .collect(Collectors.groupingBy(Promotion::getStackingGroup, Collectors.toList()));
                
            // Track usage for limits
            Map<String, Integer> groupUsageCount = new HashMap<>();
            Map<Long, Integer> customerPromoUsage = new HashMap<>();
            int orderPromoCount = 0;
            
            for (List<Promotion> groupPromos : byGroup.values()) {
                String group = groupPromos.get(0).getStackingGroup();
                int groupCount = 0;
                
                for (Promotion promo : groupPromos) {
                    // Check stacking limits
                    if (promo.getMaxStackCount() != null && groupCount >= promo.getMaxStackCount()) {
                        continue;
                    }
                    
                    if (promo.getMaxStackPerCustomer() != null) {
                        int customerUsage = customerPromoUsage.getOrDefault(promo.getId(), 0);
                        if (customerUsage >= promo.getMaxStackPerCustomer()) {
                            continue;
                        }
                    }
                    
                    if (promo.getMaxStackPerOrder() != null && orderPromoCount >= promo.getMaxStackPerOrder()) {
                        continue;
                    }
                    
                    if (isPromotionApplicable(promo, request)) {
                        ApplyPromotionResult result = applyPromotion(promo, request, response);
                        if (result.isApplied()) {
                            totalDiscount = totalDiscount.add(result.getDiscountAmount());
                            recordPromotionUsage(promo, request.getCustomerId());
                            AppliedPromotion appliedPromo = new AppliedPromotion();
                            appliedPromo.setPromoCode(promo.getPromoCode());
                            appliedPromo.setDescription(promo.getDescription());
                            response.getAppliedPromotions().add(appliedPromo);
                            
                            // Update usage counts
                            groupCount++;
                            groupUsageCount.merge(group, 1, Integer::sum);
                            customerPromoUsage.merge(promo.getId(), 1, Integer::sum);
                            orderPromoCount++;
                        }
                    }
                }
            }
        }
        
        // Calculate final totals
        response.setDiscountTotal(totalDiscount);
        response.setFinalTotal(originalTotal.subtract(totalDiscount));
        
        return response;
    }
    
    public List<PromotionDTO> getEligiblePromotions(ApplyPromotionRequest request) {
        List<Promotion> activePromotions = promotionRepository.findActivePromotions(LocalDateTime.now());
        
        return activePromotions.stream()
                .filter(promotion -> isPromotionApplicable(promotion, request))
                .map(this::convertToPromotionDTO)
                .collect(Collectors.toList());
    }
    
    public boolean validatePromotionCode(String promoCode, ApplyPromotionRequest request) {
        Optional<Promotion> promotionOpt = promotionRepository.findByPromoCodeAndIsActiveTrue(promoCode);
        
        if (promotionOpt.isEmpty()) {
            return false;
        }
        
        Promotion promotion = promotionOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        // Check date validity
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            return false;
        }
        
        return isPromotionApplicable(promotion, request);
    }
    
    private boolean isPromotionApplicable(Promotion promotion, ApplyPromotionRequest request) {
        if (promotion.getRules() == null || promotion.getRules().isEmpty()) {
            return false;
        }
        
        // At least one rule must be satisfied
        return promotion.getRules().stream()
                .anyMatch(rule -> conditionEvaluator.evaluateRule(rule, request));
    }
    
    private ApplyPromotionResult applyPromotion(Promotion promotion, ApplyPromotionRequest request, ApplyPromotionResponse response) {
        ApplyPromotionResult result = new ApplyPromotionResult();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (PromotionRule rule : promotion.getRules()) {
            if (conditionEvaluator.evaluateRule(rule, request)) {
                BigDecimal ruleDiscount = rewardApplicator.applyRuleRewards(rule, request, response);
                totalDiscount = totalDiscount.add(ruleDiscount);
            }
        }
        
        result.setApplied(totalDiscount.compareTo(BigDecimal.ZERO) > 0);
        result.setDiscountAmount(totalDiscount);
        
        return result;
    }
    
    private void recordPromotionUsage(Promotion promotion, Long customerId) {
        PromotionCustomerUsage usage = new PromotionCustomerUsage();
        usage.setPromotion(promotion);
        usage.setCustomerId(customerId);
        usageRepository.save(usage);
    }
    
    private List<LineItem> initializeLineItems(List<CartItem> cartItems) {
        return cartItems.stream().map(cartItem -> {
            LineItem lineItem = new LineItem();
            lineItem.setProductId(cartItem.getProductId());
            lineItem.setProductName(cartItem.getProductName());
            lineItem.setQuantity(cartItem.getQuantity());
            
            BigDecimal originalPrice = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            lineItem.setOriginalPrice(originalPrice);
            lineItem.setTotalDiscount(BigDecimal.ZERO);
            lineItem.setFinalPrice(originalPrice);
            
            return lineItem;
        }).collect(Collectors.toList());
    }
    
    private BigDecimal calculateOriginalTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private PromotionDTO convertToPromotionDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setPromoCode(promotion.getPromoCode());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        return dto;
    }
    
    // Inner class for apply promotion result
    private static class ApplyPromotionResult {
        private boolean applied;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        
        public boolean isApplied() { return applied; }
        public void setApplied(boolean applied) { this.applied = applied; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    }
}
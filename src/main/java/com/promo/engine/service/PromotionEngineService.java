package com.promo.engine.service;

import com.promo.engine.dto.*;
import com.promo.engine.domain.PromotionEntity;
import com.promo.engine.domain.PromotionCustomerUsage;
import com.promo.engine.domain.RuleEntity;
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
        List<PromotionEntity> activePromotions = promotionRepository.findActivePromotions(LocalDateTime.now());
        
        // Sort by priority (highest first)
        activePromotions.sort((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()));
        
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        // First, try to apply exclusive promotions
        for (PromotionEntity promotion : activePromotions) {
            if (!promotion.isExclusive()) {
                continue;
            }
            
            if (isPromotionApplicable(promotion, request)) {
                ApplyPromotionResult result = applyPromotion(promotion, request, response);
                if (result.isApplied()) {
                    totalDiscount = totalDiscount.add(result.getDiscountAmount());
                    recordPromotionUsage(promotion, request.getCustomerId());
                    addAppliedPromotion(response, promotion, result.getDiscountAmount());
                    // Stop processing - exclusive promotion applied
                    response.setFinalTotal(originalTotal.subtract(totalDiscount));
                    return response;
                }
            }
        }
        
        // If no exclusive promotion was applied, process non-exclusive promotions
        for (PromotionEntity promotion : activePromotions) {
            if (promotion.isExclusive()) {
                continue;
            }
            
            // Apply stacking rules
            if (!canStackPromotion(promotion, response.getAppliedPromotions())) {
                continue;
            }
            
            if (isPromotionApplicable(promotion, request)) {
                ApplyPromotionResult result = applyPromotion(promotion, request, response);
                if (result.isApplied()) {
                    totalDiscount = totalDiscount.add(result.getDiscountAmount());
                    recordPromotionUsage(promotion, request.getCustomerId());
                    addAppliedPromotion(response, promotion, result.getDiscountAmount());
                }
            }
        }
        
        response.setDiscountTotal(totalDiscount);
        response.setFinalTotal(originalTotal.subtract(totalDiscount));
        
        return response;
    }
    
    public List<PromotionDTO> getEligiblePromotions(ApplyPromotionRequest request) {
        List<PromotionEntity> activePromotions = promotionRepository.findActivePromotions(LocalDateTime.now());
        
        return activePromotions.stream()
                .filter(promotion -> isPromotionApplicable(promotion, request))
                .map(this::convertToPromotionDTO)
                .collect(Collectors.toList());
    }
    
    public boolean validatePromotionCode(String promoCode, ApplyPromotionRequest request) {
        Optional<PromotionEntity> promotionOpt = promotionRepository.findByPromoCodeAndActiveIsTrue(promoCode);
        
        if (promotionOpt.isEmpty()) {
            return false;
        }
        
        PromotionEntity promotion = promotionOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        // Check date validity
        if (now.isBefore(promotion.getStartDate()) || now.isAfter(promotion.getEndDate())) {
            return false;
        }
        
        return isPromotionApplicable(promotion, request);
    }
    
    private boolean isPromotionApplicable(PromotionEntity promotion, ApplyPromotionRequest request) {
        if (promotion.getRules() == null || promotion.getRules().isEmpty()) {
            return false;
        }
        
        // At least one rule must be satisfied
        return promotion.getRules().stream()
                .filter(rule -> rule instanceof RuleEntity)
                .map(rule -> (RuleEntity) rule)
                .anyMatch(rule -> conditionEvaluator.evaluateRule(rule, request));
    }
    
    private ApplyPromotionResult applyPromotion(PromotionEntity promotion, ApplyPromotionRequest request, ApplyPromotionResponse response) {
        ApplyPromotionResult result = new ApplyPromotionResult();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (PromotionRule rule : promotion.getRules()) {
            if (!(rule instanceof RuleEntity)) {
                log.warn("Skipping non-RuleEntity rule: {}", rule.getClass());
                continue;
            }
            RuleEntity ruleEntity = (RuleEntity) rule;
            if (conditionEvaluator.evaluateRule(ruleEntity, request)) {
                BigDecimal ruleDiscount = rewardApplicator.applyRuleRewards(ruleEntity, request, response);
                totalDiscount = totalDiscount.add(ruleDiscount);
            }
        }
        
        result.setApplied(totalDiscount.compareTo(BigDecimal.ZERO) > 0);
        result.setDiscountAmount(totalDiscount);
        
        return result;
    }
    
    private void recordPromotionUsage(PromotionEntity promotion, Long customerId) {
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
    
    private PromotionDTO convertToPromotionDTO(PromotionEntity promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setPromoCode(promotion.getPromoCode());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        return dto;
    }
    
    private boolean canStackPromotion(PromotionEntity promotion, List<AppliedPromotion> appliedPromotions) {
        // Check if we've reached the maximum stack count for this promotion's group
        long groupCount = appliedPromotions.stream()
            .filter(ap -> ap.getStackingGroup().equals(promotion.getStackingGroup()))
            .count();
            
        if (promotion.getMaxStackCount() != null && groupCount >= promotion.getMaxStackCount()) {
            return false;
        }
        
        // Check if we've reached the maximum promotions per order
        if (promotion.getMaxStackPerOrder() != null && appliedPromotions.size() >= promotion.getMaxStackPerOrder()) {
            return false;
        }
        
        return true;
    }
    
    private void addAppliedPromotion(ApplyPromotionResponse response, PromotionEntity promotion, BigDecimal discountAmount) {
        AppliedPromotion appliedPromo = new AppliedPromotion();
        appliedPromo.setPromotionId(promotion.getId());
        appliedPromo.setPromoCode(promotion.getPromoCode());
        appliedPromo.setName(promotion.getName());
        appliedPromo.setDescription(promotion.getDescription());
        appliedPromo.setDiscountAmount(discountAmount);
        appliedPromo.setStackingGroup(promotion.getStackingGroup());
        response.getAppliedPromotions().add(appliedPromo);
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
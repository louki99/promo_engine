package com.promo.engine.service.impl;

import com.promo.engine.dto.ApplyPromotionRequest;
import com.promo.engine.dto.CartItem;
import com.promo.engine.entity.Condition;
import com.promo.engine.entity.PromotionRule;
import com.promo.engine.enums.ConditionLogic;
import com.promo.engine.enums.ConditionType;
import com.promo.engine.enums.OperatorType;
import com.promo.engine.repository.PromotionCustomerUsageRepository;
import com.promo.engine.service.ConditionEvaluatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConditionEvaluatorServiceImpl implements ConditionEvaluatorService {

    private final PromotionCustomerUsageRepository usageRepository;

    @Override
    public boolean evaluateRule(PromotionRule rule, ApplyPromotionRequest request) {
        if (rule.getConditions() == null || rule.getConditions().isEmpty()) {
            return false;
        }

        boolean result = rule.getConditionLogic() == ConditionLogic.ALL;
        
        for (Condition condition : rule.getConditions()) {
            boolean conditionResult = evaluateCondition(condition, request);
            
            if (rule.getConditionLogic() == ConditionLogic.ALL) {
                result = result && conditionResult;
                if (!result) break; // Short circuit for ALL logic
            } else { // ANY logic
                result = result || conditionResult;
                if (result) break; // Short circuit for ANY logic
            }
        }
        
        return result;
    }

    private boolean evaluateCondition(Condition condition, ApplyPromotionRequest request) {
        switch (condition.getConditionType()) {
            case PRODUCT_IN_CART:
                return evaluateProductInCart(condition, request.getCartItems());
            case CART_SUBTOTAL:
                return evaluateCartSubtotal(condition, request.getCartItems());
            case CUSTOMER_IN_GROUP:
                return evaluateCustomerInGroup(condition, request.getCustomerId());
            case PRODUCT_QUANTITY:
                return evaluateProductQuantity(condition, request.getCartItems());
            case TOTAL_PROMOTION_USAGE:
                return evaluateTotalPromotionUsage(condition, request.getCustomerId());
            default:
                return false;
        }
    }

    private boolean evaluateProductInCart(Condition condition, List<CartItem> cartItems) {
        if (condition.getEntityType().equals("PRODUCT")) {
            return cartItems.stream()
                    .anyMatch(item -> item.getProductId().equals(condition.getEntityId()));
        } else if (condition.getEntityType().equals("PRODUCT_FAMILY")) {
            return cartItems.stream()
                    .anyMatch(item -> item.getFamilyId() != null && 
                            item.getFamilyId().equals(String.valueOf(condition.getEntityId())));
        }
        return false;
    }

    private boolean evaluateCartSubtotal(Condition condition, List<CartItem> cartItems) {
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return compareValues(subtotal, new BigDecimal(condition.getValue()), condition.getOperator());
    }

    private boolean evaluateCustomerInGroup(Condition condition, Long customerId) {
        // This would typically involve checking if the customer is in a specific group
        // For now, we'll return false as this would require additional customer group management
        return false;
    }

    private boolean evaluateProductQuantity(Condition condition, List<CartItem> cartItems) {
        if (condition.getEntityType().equals("PRODUCT")) {
            int totalQuantity = cartItems.stream()
                    .filter(item -> item.getProductId().equals(condition.getEntityId()))
                    .mapToInt(CartItem::getQuantity)
                    .sum();
            
            return compareValues(BigDecimal.valueOf(totalQuantity), 
                    new BigDecimal(condition.getValue()), condition.getOperator());
        }
        return false;
    }

    private boolean evaluateTotalPromotionUsage(Condition condition, Long customerId) {
        Long usageCount = usageRepository.countUsageByPromotionIdAndCustomerId(
                condition.getEntityId(), customerId);
        
        return compareValues(BigDecimal.valueOf(usageCount), 
                new BigDecimal(condition.getValue()), condition.getOperator());
    }

    private boolean compareValues(BigDecimal actual, BigDecimal expected, OperatorType operator) {
        switch (operator) {
            case GREATER_THAN:
                return actual.compareTo(expected) > 0;
            case GREATER_THAN_OR_EQUAL:
                return actual.compareTo(expected) >= 0;
            case LESS_THAN:
                return actual.compareTo(expected) < 0;
            case LESS_THAN_OR_EQUAL:
                return actual.compareTo(expected) <= 0;
            case EQUAL:
                return actual.compareTo(expected) == 0;
            case NOT_EQUAL:
                return actual.compareTo(expected) != 0;
            default:
                return false;
        }
    }
} 
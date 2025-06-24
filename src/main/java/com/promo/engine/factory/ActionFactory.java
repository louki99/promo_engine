package com.promo.engine.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promo.engine.domain.ActionEntity;
import com.promo.engine.action.Action;
import com.promo.engine.action.SetPriceAction;
import com.promo.engine.action.PercentageDiscountAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ActionFactory {
    private final ObjectMapper objectMapper;

    public ActionFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Action createAction(ActionEntity entity) {
        try {
            Map<String, Object> parameters = objectMapper.readValue(entity.getParameters(), Map.class);
            
            return switch (entity.getType()) {
                case "SET_PRICE" -> createSetPriceAction(parameters);
                case "PERCENTAGE_DISCOUNT" -> createPercentageDiscountAction(parameters);
                default -> throw new IllegalArgumentException("Unknown action type: " + entity.getType());
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to create action from entity", e);
        }
    }

    private SetPriceAction createSetPriceAction(Map<String, Object> parameters) {
        String productId = (String) parameters.get("productId");
        double newPrice = ((Number) parameters.get("newPrice")).doubleValue();
        return new SetPriceAction(productId, newPrice);
    }

    private PercentageDiscountAction createPercentageDiscountAction(Map<String, Object> parameters) {
        String productId = (String) parameters.get("productId");
        String category = (String) parameters.get("category");
        double discountPercentage = ((Number) parameters.get("discountPercentage")).doubleValue();
        
        if (productId != null) {
            return new PercentageDiscountAction(productId, discountPercentage);
        } else {
            return new PercentageDiscountAction(category, discountPercentage, true);
        }
    }
} 
package com.promo.engine.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promo.engine.domain.RuleEntity;
import com.promo.engine.rule.Rule;
import com.promo.engine.rule.BasketCompositionRule;
import com.promo.engine.rule.ProductQuantityRule;
import com.promo.engine.rule.BasketValueRule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleFactory {
    private final ObjectMapper objectMapper;

    public RuleFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Rule createRule(RuleEntity entity) {
        try {
            Map<String, Object> parameters = objectMapper.readValue(entity.getParameters(), Map.class);
            
            return switch (entity.getType()) {
                case "BASKET_COMPOSITION" -> createBasketCompositionRule(parameters);
                case "PRODUCT_QUANTITY" -> createProductQuantityRule(parameters);
                case "BASKET_VALUE" -> createBasketValueRule(parameters);
                default -> throw new IllegalArgumentException("Unknown rule type: " + entity.getType());
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to create rule from entity", e);
        }
    }

    private BasketCompositionRule createBasketCompositionRule(Map<String, Object> parameters) {
        String category = (String) parameters.get("category");
        int minDistinctProducts = ((Number) parameters.get("minDistinctProducts")).intValue();
        int minQuantityPerProduct = ((Number) parameters.getOrDefault("minQuantityPerProduct", 1)).intValue();
        return new BasketCompositionRule(category, minDistinctProducts, minQuantityPerProduct);
    }

    private ProductQuantityRule createProductQuantityRule(Map<String, Object> parameters) {
        String productId = (String) parameters.get("productId");
        String category = (String) parameters.get("category");
        int minQuantity = ((Number) parameters.get("minQuantity")).intValue();
        int maxQuantity = ((Number) parameters.getOrDefault("maxQuantity", Integer.MAX_VALUE)).intValue();
        
        if (productId != null) {
            return new ProductQuantityRule(productId, null, minQuantity, maxQuantity);
        } else {
            return new ProductQuantityRule(null, category, minQuantity, maxQuantity);
        }
    }

    private BasketValueRule createBasketValueRule(Map<String, Object> parameters) {
        double minimumValue = ((Number) parameters.get("minimumValue")).doubleValue();
        return new BasketValueRule(minimumValue);
    }
}
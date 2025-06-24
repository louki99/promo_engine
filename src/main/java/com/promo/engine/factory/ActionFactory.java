package com.promo.engine.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promo.engine.domain.ActionEntity;
import com.promo.engine.action.Action;
import com.promo.engine.action.SetPriceAction;
import com.promo.engine.action.PercentageDiscountAction;
import com.promo.engine.validation.ParameterSchemaValidator;
import com.promo.engine.validation.SchemaRegistry;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ActionFactory {
    private final ObjectMapper objectMapper;
    private final ParameterSchemaValidator validator;
    private final SchemaRegistry schemaRegistry;

    public ActionFactory(ObjectMapper objectMapper, ParameterSchemaValidator validator, SchemaRegistry schemaRegistry) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.schemaRegistry = schemaRegistry;
    }

    public Action createAction(ActionEntity entity) {
        try {
            // Validate parameters against schema
            validator.validateParameters(entity.getType(), entity.getParameters(), schemaRegistry.getActionSchema(entity.getType()));
            
            // Parse parameters
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
        String category = (String) parameters.get("category");
        double newPrice = ((Number) parameters.get("newPrice")).doubleValue();
        Double minPrice = parameters.get("minPrice") != null ? ((Number) parameters.get("minPrice")).doubleValue() : null;
        Double maxPrice = parameters.get("maxPrice") != null ? ((Number) parameters.get("maxPrice")).doubleValue() : null;
        
        if (productId != null) {
            return new SetPriceAction(productId, null, newPrice, minPrice, maxPrice);
        } else {
            return new SetPriceAction(null, category, newPrice, minPrice, maxPrice);
        }
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
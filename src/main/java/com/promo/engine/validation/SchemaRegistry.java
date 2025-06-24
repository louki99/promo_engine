package com.promo.engine.validation;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SchemaRegistry {
    private final Map<String, String> ruleSchemas = new ConcurrentHashMap<>();
    private final Map<String, String> actionSchemas = new ConcurrentHashMap<>();

    public SchemaRegistry() {
        // Register rule schemas
        registerRuleSchema("BASKET_COMPOSITION", """
            {
                "type": "object",
                "properties": {
                    "category": { "type": ["string", "null"] },
                    "minDistinctProducts": { "type": "integer", "minimum": 1 },
                    "minQuantityPerProduct": { "type": "integer", "minimum": 1 }
                },
                "required": ["minDistinctProducts"]
            }
            """);

        registerRuleSchema("PRODUCT_QUANTITY", """
            {
                "type": "object",
                "properties": {
                    "productId": { "type": ["string", "null"] },
                    "category": { "type": ["string", "null"] },
                    "minQuantity": { "type": "integer", "minimum": 1 },
                    "maxQuantity": { "type": "integer", "minimum": 1 }
                },
                "required": ["minQuantity"],
                "oneOf": [
                    { "required": ["productId"] },
                    { "required": ["category"] }
                ]
            }
            """);

        registerRuleSchema("BASKET_VALUE", """
            {
                "type": "object",
                "properties": {
                    "minimumValue": { "type": "number", "minimum": 0 }
                },
                "required": ["minimumValue"]
            }
            """);

        // Register action schemas
        registerActionSchema("SET_PRICE", """
            {
                "type": "object",
                "properties": {
                    "productId": { "type": ["string", "null"] },
                    "category": { "type": ["string", "null"] },
                    "newPrice": { "type": "number", "minimum": 0 },
                    "minPrice": { "type": ["number", "null"], "minimum": 0 },
                    "maxPrice": { "type": ["number", "null"], "minimum": 0 }
                },
                "required": ["newPrice"],
                "oneOf": [
                    { "required": ["productId"] },
                    { "required": ["category"] }
                ]
            }
            """);

        registerActionSchema("PERCENTAGE_DISCOUNT", """
            {
                "type": "object",
                "properties": {
                    "productId": { "type": ["string", "null"] },
                    "category": { "type": ["string", "null"] },
                    "discountPercentage": { "type": "number", "minimum": 0, "maximum": 100 }
                },
                "required": ["discountPercentage"],
                "oneOf": [
                    { "required": ["productId"] },
                    { "required": ["category"] }
                ]
            }
            """);
    }

    public void registerRuleSchema(String type, String schema) {
        ruleSchemas.put(type, schema);
    }

    public void registerActionSchema(String type, String schema) {
        actionSchemas.put(type, schema);
    }

    public String getRuleSchema(String type) {
        String schema = ruleSchemas.get(type);
        if (schema == null) {
            throw new IllegalArgumentException("No schema registered for rule type: " + type);
        }
        return schema;
    }

    public String getActionSchema(String type) {
        String schema = actionSchemas.get(type);
        if (schema == null) {
            throw new IllegalArgumentException("No schema registered for action type: " + type);
        }
        return schema;
    }
} 
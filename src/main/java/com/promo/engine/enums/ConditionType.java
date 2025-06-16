package com.promo.engine.enums;

public enum ConditionType {
    // Basic conditions
    PRODUCT_IN_CART,           // Product exists in cart
    CART_SUBTOTAL,            // Cart total meets threshold
    CUSTOMER_IN_GROUP,        // Customer belongs to group
    PRODUCT_QUANTITY,         // Product quantity meets threshold
    TOTAL_PROMOTION_USAGE,    // Total promotion usage meets threshold
    
    // Advanced conditions
    TIME_OF_DAY,              // Time-based conditions
    DAY_OF_WEEK,             // Day-based conditions
    CUSTOMER_SEGMENT,         // Customer segment conditions
    PRODUCT_ATTRIBUTE,        // Product attribute conditions
    CART_COMPOSITION,         // Cart composition conditions
    CUSTOMER_HISTORY,         // Customer purchase history
    SEASONAL_FACTOR,          // Seasonal conditions
    LOYALTY_LEVEL,            // Customer loyalty level
    BUNDLE_REQUIREMENT,       // Bundle requirements
    CROSS_PROMOTION,          // Cross-promotion conditions
    DYNAMIC_PRICING,          // Dynamic pricing conditions
    INVENTORY_LEVEL           // Inventory level conditions
} 
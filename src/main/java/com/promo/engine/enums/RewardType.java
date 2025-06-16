package com.promo.engine.enums;

public enum RewardType {
    // Basic discount types
    PERCENT_DISCOUNT_ON_ITEM,
    PERCENT_DISCOUNT_ON_CART,
    FIXED_DISCOUNT_ON_ITEM,
    FIXED_DISCOUNT_ON_CART,
    
    // Product rewards
    FREE_PRODUCT,
    FREE_SHIPPING,
    
    // Complex scenarios
    BUNDLE_DISCOUNT,           // For "Buy X get Y" scenarios
    LOYALTY_MULTIPLIER,        // For loyalty program integration
    DYNAMIC_PRICING,          // For dynamic pricing based on demand
    CROSS_PROMOTION_DISCOUNT,  // For combining multiple promotions
    TIME_BASED_DISCOUNT,      // For time-sensitive promotions
    CUSTOMER_SEGMENT_DISCOUNT, // For customer segment specific discounts
    PRODUCT_ATTRIBUTE_DISCOUNT // For attribute-based discounts
} 
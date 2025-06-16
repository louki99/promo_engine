package com.promo.engine.enums;

public enum BreakpointType {
    // Basic breakpoint types
    AMOUNT,                 // Based on cart total amount
    QUANTITY,              // Based on item quantity
    SKU_POINTS,            // Based on SKU attributes
    
    // Advanced breakpoint types
    PROMOTION_TOTAL_USAGE, // Based on total promotion usage
    CUSTOMER_TOTAL_SPEND,  // Based on customer's total spend
    TIME_BASED,            // Based on time of day/week
    CUSTOMER_SEGMENT,      // Based on customer segment
    PRODUCT_ATTRIBUTE,     // Based on product attributes
    CART_COMPOSITION,      // Based on cart composition
    LOYALTY_POINTS,        // Based on customer loyalty points
    SEASONAL_FACTOR        // Based on seasonal factors
} 
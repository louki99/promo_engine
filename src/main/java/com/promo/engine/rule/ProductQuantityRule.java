package com.promo.engine.rule;

import com.promo.engine.domain.Cart;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductQuantityRule implements Rule {
    private final String productId;
    private final String category;
    private final int minQuantity;

    public ProductQuantityRule(String productId, int minQuantity) {
        this.productId = productId;
        this.category = null;
        this.minQuantity = minQuantity;
    }

    public ProductQuantityRule(String category, int minQuantity, boolean isCategory) {
        this.productId = null;
        this.category = category;
        this.minQuantity = minQuantity;
    }

    @Override
    public boolean evaluate(Cart cart) {
        if (productId != null) {
            return cart.getQuantityForProduct(productId) >= minQuantity;
        } else {
            return cart.getQuantityForCategory(category) >= minQuantity;
        }
    }
} 
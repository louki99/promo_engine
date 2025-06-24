package com.promo.engine.rule;

import com.promo.engine.domain.Cart;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductQuantityRule implements Rule {
    private final String productId;
    private final String category;
    private final int minQuantity;
    private final int maxQuantity;

    public ProductQuantityRule(String productId, int minQuantity) {
        this(productId, null, minQuantity, Integer.MAX_VALUE);
    }

    public ProductQuantityRule(String category, int minQuantity, boolean isCategory) {
        this(null, category, minQuantity, Integer.MAX_VALUE);
    }

    @Override
    public boolean evaluate(Cart cart) {
        if (productId != null) {
            // Check quantity for specific product
            int quantity = cart.getItems().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .mapToInt(item -> item.getQuantity())
                .sum();
            return quantity >= minQuantity && quantity <= maxQuantity;
        } else if (category != null) {
            // Check quantity for category
            int quantity = cart.getItems().stream()
                .filter(item -> category.equals(item.getCategory()))
                .mapToInt(item -> item.getQuantity())
                .sum();
            return quantity >= minQuantity && quantity <= maxQuantity;
        }
        return false;
    }
} 
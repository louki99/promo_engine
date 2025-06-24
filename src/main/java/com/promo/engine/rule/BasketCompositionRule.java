package com.promo.engine.rule;

import com.promo.engine.domain.Cart;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasketCompositionRule implements Rule {
    private final String category;
    private final int minDistinctProducts;
    private final int minQuantityPerProduct;

    @Override
    public boolean evaluate(Cart cart) {
        if (category != null) {
            // Check distinct products count in specific category
            long distinctProductsInCategory = cart.getItems().stream()
                .filter(item -> category.equals(item.getCategory()))
                .filter(item -> item.getQuantity() >= minQuantityPerProduct)
                .count();
            return distinctProductsInCategory >= minDistinctProducts;
        } else {
            // Check distinct products count across all categories
            long distinctProducts = cart.getItems().stream()
                .filter(item -> item.getQuantity() >= minQuantityPerProduct)
                .count();
            return distinctProducts >= minDistinctProducts;
        }
    }
} 
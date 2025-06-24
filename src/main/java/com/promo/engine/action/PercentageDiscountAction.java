package com.promo.engine.action;

import com.promo.engine.domain.Cart;
import com.promo.engine.domain.CartItem;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PercentageDiscountAction implements Action {
    private final String productId;
    private final String category;
    private final double discountPercentage;

    public PercentageDiscountAction(String productId, double discountPercentage) {
        this.productId = productId;
        this.category = null;
        this.discountPercentage = discountPercentage;
    }

    public PercentageDiscountAction(String category, double discountPercentage, boolean isCategory) {
        this.productId = null;
        this.category = category;
        this.discountPercentage = discountPercentage;
    }

    @Override
    public void apply(Cart cart) {
        cart.getItems().stream()
            .filter(item -> matches(item))
            .forEach(item -> {
                double currentPrice = item.getPrice();
                double discountAmount = currentPrice * (discountPercentage / 100.0);
                item.setPrice(currentPrice - discountAmount);
            });
    }

    private boolean matches(CartItem item) {
        if (productId != null) {
            return item.getProductId().equals(productId);
        } else {
            return item.getCategory().equals(category);
        }
    }
} 
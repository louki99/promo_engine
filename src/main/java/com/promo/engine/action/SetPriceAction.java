package com.promo.engine.action;

import com.promo.engine.domain.Cart;
import com.promo.engine.domain.CartItem;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SetPriceAction implements Action {
    private final String productId;
    private final double newPrice;

    @Override
    public void apply(Cart cart) {
        cart.getItems().stream()
            .filter(item -> item.getProductId().equals(productId))
            .forEach(item -> item.setPrice(newPrice));
    }
} 
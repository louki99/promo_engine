package com.promo.engine.rule;

import com.promo.engine.domain.Cart;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasketCompositionRule implements Rule {
    private final int distinctProducts;

    @Override
    public boolean evaluate(Cart cart) {
        return cart.getDistinctProductCount() >= distinctProducts;
    }
} 
package com.promo.engine.rule;

import com.promo.engine.domain.Cart;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasketValueRule implements Rule {
    private final double minimumValue;

    @Override
    public boolean evaluate(Cart cart) {
        double totalValue = cart.getItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        return totalValue >= minimumValue;
    }
} 
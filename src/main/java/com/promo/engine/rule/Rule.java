package com.promo.engine.rule;

import com.promo.engine.domain.Cart;

public interface Rule {
    boolean evaluate(Cart cart);
} 
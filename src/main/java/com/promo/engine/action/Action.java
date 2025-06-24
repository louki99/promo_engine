package com.promo.engine.action;

import com.promo.engine.domain.Cart;

public interface Action {
    void apply(Cart cart);
} 
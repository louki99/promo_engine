package com.promo.engine.action;

import com.promo.engine.domain.Cart;
import com.promo.engine.domain.CartItem;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class SetPriceAction implements Action {
    private final String productId;
    private final String category;
    private final BigDecimal newPrice;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;

    public SetPriceAction(String productId, BigDecimal newPrice) {
        this(productId, null, newPrice, null, null);
    }

    public SetPriceAction(String category, BigDecimal newPrice, boolean isCategory) {
        this(null, category, newPrice, null, null);
    }

    @Override
    public void apply(Cart cart) {
        if (productId != null) {
            // Set price for specific product
            cart.getItems().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .forEach(item -> setPriceWithinBounds(item));
        } else if (category != null) {
            // Set price for all products in category
            cart.getItems().stream()
                .filter(item -> category.equals(item.getCategory()))
                .forEach(item -> setPriceWithinBounds(item));
        }
    }

    private void setPriceWithinBounds(CartItem item) {
        BigDecimal finalPrice = newPrice;
        if (minPrice != null && finalPrice.compareTo(minPrice) < 0) {
            finalPrice = minPrice;
        }
        if (maxPrice != null && finalPrice.compareTo(maxPrice) > 0) {
            finalPrice = maxPrice;
        }
        item.setPrice(finalPrice.doubleValue());
    }
} 
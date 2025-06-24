package com.promo.engine.domain;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Cart {
    private List<CartItem> items;

    public int getDistinctProductCount() {
        return items.stream()
            .map(CartItem::getProductId)
            .collect(Collectors.toSet())
            .size();
    }

    public int getQuantityForProduct(String productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    public int getQuantityForCategory(String category) {
        return items.stream()
            .filter(item -> item.getCategory().equals(category))
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    public Map<String, Double> getProductPrices() {
        return items.stream()
            .collect(Collectors.toMap(
                CartItem::getProductId,
                CartItem::getPrice,
                (price1, price2) -> price1 // In case of duplicates, keep the first price
            ));
    }
} 
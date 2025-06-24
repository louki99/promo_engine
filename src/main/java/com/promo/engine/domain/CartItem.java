package com.promo.engine.domain;

import lombok.Data;

@Data
public class CartItem {
    private String productId;
    private String category;
    private int quantity;
    private double price;
} 
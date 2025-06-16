package com.promo.engine.dto;

import com.promo.engine.entity.CustomerHistory;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerHistoryUpdateRequest {
    private BigDecimal orderTotal;
    private List<CustomerHistory.ProductPurchase> purchases;
} 
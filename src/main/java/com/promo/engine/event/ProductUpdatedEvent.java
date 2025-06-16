package com.promo.engine.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedEvent {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Long familyId;
    private String familyName;
    private BigDecimal skuPoints;
    private Long categoryId;
    private String categoryName;
    private Boolean isActive;
}

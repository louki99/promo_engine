package com.promo.engine.event.product.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime updatedAt;
}

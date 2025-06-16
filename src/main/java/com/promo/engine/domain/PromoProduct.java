package com.promo.engine.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_products")
@Getter
@Setter
@NoArgsConstructor
public class PromoProduct {
    @Id
    private Long productId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "family_id")
    private Long familyId;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "sku_points")
    private BigDecimal skuPoints;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
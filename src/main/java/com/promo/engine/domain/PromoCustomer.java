package com.promo.engine.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.math.BigDecimal;

@Entity
@Table(name = "promo_customers")
@Getter
@Setter
@NoArgsConstructor
public class PromoCustomer {
    @Id
    private Long customerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "promo_customer_family_mappings",
            joinColumns = @JoinColumn(name = "customer_id")
    )
    @Column(name = "family_id")
    private Set<Long> familyIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "promo_customer_segment_mappings",
            joinColumns = @JoinColumn(name = "customer_id")
    )
    @Column(name = "segment_id")
    private Set<Long> segmentIds = new HashSet<>();

    @Column(name = "loyalty_tier")
    private String loyaltyTier;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "total_spend")
    private BigDecimal totalSpend = BigDecimal.ZERO;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
package com.promo.engine.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdatedEvent {
    private Long customerId;
    private Set<Long> familyIds;
    private Set<Long> segmentIds;
    private String loyaltyTier;
    private Integer loyaltyPoints;
    private BigDecimal totalSpend;
}

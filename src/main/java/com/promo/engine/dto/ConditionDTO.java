package com.promo.engine.dto;

import com.promo.engine.enums.ConditionType;
import com.promo.engine.enums.OperatorType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConditionDTO {
    @NotNull(message = "Condition type is required")
    private ConditionType conditionType;
    
    @NotNull(message = "Operator is required")
    private OperatorType operator = OperatorType.GREATER_THAN_OR_EQUAL;
    
    private String value;
    
    private String entityType; // PRODUCT, PRODUCT_FAMILY, CUSTOMER_FAMILY
    
    private Long entityId;

    private Long ruleId;
} 
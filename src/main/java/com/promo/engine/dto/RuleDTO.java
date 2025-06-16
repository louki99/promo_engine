package com.promo.engine.dto;

import com.promo.engine.enums.BreakpointType;
import com.promo.engine.enums.CalculationMethod;
import com.promo.engine.enums.ConditionLogic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RuleDTO {
    @NotNull(message = "Promotion ID is required")
    private Long promotionId;
    
    @NotBlank(message = "Rule name is required")
    private String name;
    
    @NotNull(message = "Condition logic is required")
    private ConditionLogic conditionLogic = ConditionLogic.ALL;
    
    @NotNull(message = "Calculation method is required")
    private CalculationMethod calculationMethod = CalculationMethod.BRACKET;
    
    @NotNull(message = "Breakpoint type is required")
    private BreakpointType breakpointType = BreakpointType.AMOUNT;
} 
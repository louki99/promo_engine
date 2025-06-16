package com.promo.engine.dto;

import com.promo.engine.enums.FamilyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FamilyDTO {
    @NotBlank(message = "Family name is required")
    private String name;
    
    @NotNull(message = "Family type is required")
    private FamilyType familyType;
    
    private String description;
} 
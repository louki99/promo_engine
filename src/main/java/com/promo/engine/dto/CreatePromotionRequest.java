package com.promo.engine.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreatePromotionRequest {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private int priority;
    private List<RuleDto> rules;
    private List<ActionDto> actions;
}
package com.promo.engine.dto;

import lombok.Data;
import java.util.List;

@Data
public class ScenarioPromotionRequest {
    private String name;
    private int priority;
    private boolean exclusive;
    private String rulesLogic;
    private List<RuleRequest> rules;
    private List<ActionRequest> actions;

    @Data
    public static class RuleRequest {
        private String type;
        private String parameters;
    }

    @Data
    public static class ActionRequest {
        private String type;
        private String parameters;
    }
} 
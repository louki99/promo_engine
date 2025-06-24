package com.promo.engine.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ParameterSchemaValidator {
    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    public void validateParameters(String type, String parameters, String schemaContent) {
        try {
            JsonNode parametersNode = objectMapper.readTree(parameters);
            JsonSchema schema = schemaFactory.getSchema(schemaContent);
            
            Set<ValidationMessage> validationResult = schema.validate(parametersNode);
            if (!validationResult.isEmpty()) {
                String errors = validationResult.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("Invalid parameters for " + type + ": " + errors);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to validate parameters for " + type, e);
        }
    }
} 
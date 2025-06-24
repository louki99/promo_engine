package com.promo.engine.controller;

import com.promo.engine.dto.ApplyPromotionRequest;
import com.promo.engine.dto.ApplyPromotionResponse;
import com.promo.engine.service.PromotionEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/promotions/engine")
@RequiredArgsConstructor
@Tag(name = "Promotion Engine", description = "API for applying promotions to shopping carts")
public class PromotionEngineController {

    private final PromotionEngineService promotionEngineService;

    @Operation(
        summary = "Calculate applicable promotions",
        description = "Evaluates the cart contents and customer details against all active promotions and returns applicable discounts"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully calculated promotions",
            content = @Content(schema = @Schema(implementation = ApplyPromotionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        )
    })
    @PostMapping("/calculate")
    public ResponseEntity<ApplyPromotionResponse> calculatePromotions(
        @Parameter(description = "Cart and customer details for promotion calculation")
        @Valid @RequestBody ApplyPromotionRequest request
    ) {
        return ResponseEntity.ok(promotionEngineService.calculatePromotions(request));
    }

    @Operation(
        summary = "Validate promotion code",
        description = "Checks if a promotion code is valid and applicable to the given cart"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Promotion code validation result"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters"
        )
    })
    @PostMapping("/validate/{promoCode}")
    public ResponseEntity<Boolean> validatePromotionCode(
        @Parameter(description = "Promotion code to validate")
        @PathVariable String promoCode,
        @Parameter(description = "Cart and customer details for validation")
        @Valid @RequestBody ApplyPromotionRequest request
    ) {
        return ResponseEntity.ok(promotionEngineService.validatePromotionCode(promoCode, request));
    }
} 
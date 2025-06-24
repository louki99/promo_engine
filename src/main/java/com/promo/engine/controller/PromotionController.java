package com.promo.engine.controller;

import com.promo.engine.dto.ApplyPromotionRequest;
import com.promo.engine.dto.ApplyPromotionResponse;
import com.promo.engine.dto.PromotionDTO;
import com.promo.engine.service.PromotionEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionEngineService promotionEngineService;

    @PostMapping("/calculate")
    public ResponseEntity<ApplyPromotionResponse> calculatePromotion(@Valid @RequestBody ApplyPromotionRequest request) {
        return ResponseEntity.ok(promotionEngineService.calculatePromotions(request));
    }

    @PostMapping("/eligible")
    public ResponseEntity<List<PromotionDTO>> getEligiblePromotions(@Valid @RequestBody ApplyPromotionRequest request) {
        return ResponseEntity.ok(promotionEngineService.getEligiblePromotions(request));
    }

    @PostMapping("/validate/{promoCode}")
    public ResponseEntity<Boolean> validatePromoCode(
            @PathVariable String promoCode, 
            @Valid @RequestBody ApplyPromotionRequest request) {
        return ResponseEntity.ok(promotionEngineService.validatePromotionCode(promoCode, request));
    }
} 
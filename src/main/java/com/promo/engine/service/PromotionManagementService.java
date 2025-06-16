package com.promo.engine.service;

import com.promo.engine.dto.PromotionDTO;
import com.promo.engine.domain.Promotion;

import java.util.List;

public interface PromotionManagementService {
    Promotion createPromotion(PromotionDTO promotionDTO);
    List<Promotion> getAllPromotions();
    Promotion getPromotionById(Long id);
    Promotion updatePromotion(Long id, PromotionDTO promotionDTO);
    void deletePromotion(Long id);
    Promotion activatePromotion(Long id);
    Promotion deactivatePromotion(Long id);
} 
package com.promo.engine.service;

import com.promo.engine.dto.BundlePromotionDTO;
import com.promo.engine.domain.BundlePromotion;

import java.util.List;

public interface BundlePromotionService {
    BundlePromotionDTO createBundlePromotion(BundlePromotionDTO bundlePromotionDTO);
    
    BundlePromotionDTO updateBundlePromotion(Long id, BundlePromotionDTO bundlePromotionDTO);
    
    void deleteBundlePromotion(Long id);
    
    BundlePromotionDTO getBundlePromotion(Long id);
    
    List<BundlePromotionDTO> getAllBundlePromotions();
    
    List<BundlePromotionDTO> getActiveBundlePromotions();
    
    List<BundlePromotionDTO> getBundlePromotionsByPromotionId(Long promotionId);
    
    boolean isBundleComplete(Long bundleId, List<BundlePromotion.BundleItem> cartItems);
    
    BundlePromotionDTO calculateBundleDiscount(Long bundleId, List<BundlePromotion.BundleItem> cartItems);
} 
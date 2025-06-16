package com.promo.engine.repository;

import com.promo.engine.entity.BundlePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundlePromotionRepository extends JpaRepository<BundlePromotion, Long> {
    List<BundlePromotion> findByIsActiveTrue();
    List<BundlePromotion> findByPromotionId(Long promotionId);
} 
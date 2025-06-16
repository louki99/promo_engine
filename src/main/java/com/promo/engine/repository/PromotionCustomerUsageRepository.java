package com.promo.engine.repository;

import com.promo.engine.entity.PromotionCustomerUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionCustomerUsageRepository extends JpaRepository<PromotionCustomerUsage, Long> {
    
    @Query("SELECT COUNT(u) FROM PromotionCustomerUsage u WHERE u.promotion.id = :promotionId")
    Long countTotalUsageByPromotionId(@Param("promotionId") Long promotionId);
    
    @Query("SELECT COUNT(u) FROM PromotionCustomerUsage u WHERE u.promotion.id = :promotionId AND u.customerId = :customerId")
    Long countUsageByPromotionIdAndCustomerId(@Param("promotionId") Long promotionId, @Param("customerId") Long customerId);
} 
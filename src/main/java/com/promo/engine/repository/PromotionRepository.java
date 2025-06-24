package com.promo.engine.repository;

import com.promo.engine.domain.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {
    @Query("SELECT p FROM PromotionEntity p WHERE p.active = true AND p.startDate <= :now AND p.endDate >= :now ORDER BY p.priority DESC")
    List<PromotionEntity> findActivePromotions(@Param("now") LocalDateTime now);

    Optional<PromotionEntity> findByPromoCodeAndActiveIsTrue(String promoCode);

    List<PromotionEntity> findByActiveIsTrueAndStartDateBeforeAndEndDateAfterOrderByPriorityDesc(
        LocalDateTime now,
        LocalDateTime now2
    );
} 
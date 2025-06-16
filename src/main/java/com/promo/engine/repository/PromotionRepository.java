package com.promo.engine.repository;

import com.promo.engine.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.startDate <= :now AND p.endDate >= :now ORDER BY p.priority DESC")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);
    
    Optional<Promotion> findByPromoCodeAndIsActiveTrue(String promoCode);
    
    @Query("SELECT p FROM Promotion p LEFT JOIN FETCH p.rules r LEFT JOIN FETCH r.conditions LEFT JOIN FETCH r.tiers t LEFT JOIN FETCH t.reward WHERE p.id = :id")
    Optional<Promotion> findByIdWithDetails(@Param("id") Long id);
} 
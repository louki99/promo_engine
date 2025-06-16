package com.promo.engine.repository;

import com.promo.engine.domain.PromoProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoProductRepository extends JpaRepository<PromoProduct, Long>{

    @Query ("SELECT p FROM PromoProduct p WHERE p.isActive = true AND p.productId IN :productIds")
    List<PromoProduct> findAllActiveByIds(@Param ("productIds") List<Long> productIds);

    @Query("SELECT p FROM PromoProduct p WHERE p.isActive = true AND p.familyId = :familyId")
    List<PromoProduct> findAllActiveByFamilyId(@Param("familyId") Long familyId);

    @Query("SELECT p FROM PromoProduct p WHERE p.isActive = true AND p.categoryId = :categoryId")
    List<PromoProduct> findAllActiveByCategoryId(@Param("categoryId") Long categoryId);

    Optional<PromoProduct> findByProductIdAndIsActiveTrue(Long productId);
}

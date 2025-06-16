package com.promo.engine.repository;

import com.promo.engine.entity.Family;
import com.promo.engine.enums.FamilyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    
    Optional<Family> findByName(String name);
    
    List<Family> findByFamilyType(FamilyType familyType);
    
    @Query("SELECT f FROM Family f LEFT JOIN FETCH f.members WHERE f.id = :id")
    Optional<Family> findByIdWithMembers(@Param("id") Long id);
} 
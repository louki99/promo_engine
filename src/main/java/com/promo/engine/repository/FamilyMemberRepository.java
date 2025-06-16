package com.promo.engine.repository;

import com.promo.engine.domain.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    
    @Query("SELECT fm.memberId FROM FamilyMember fm WHERE fm.family.id = :familyId")
    List<Long> findMemberIdsByFamilyId(Long familyId);
    
    @Modifying
    @Query("DELETE FROM FamilyMember fm WHERE fm.family.id = :familyId AND fm.memberId IN :memberIds")
    void deleteByFamilyIdAndMemberIdIn(Long familyId, List<Long> memberIds);
} 
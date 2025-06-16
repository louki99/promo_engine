package com.promo.engine.service;

import com.promo.engine.dto.FamilyDTO;
import com.promo.engine.domain.Family;

import java.util.List;

public interface FamilyService {
    Family createFamily(FamilyDTO familyDTO);
    
    List<Family> getAllFamilies();
    
    Family getFamilyById(Long id);
    
    List<Family> getFamiliesByType(String familyType);
    
    Family updateFamily(Long id, FamilyDTO familyDTO);
    
    void deleteFamily(Long id);
    
    Family addMembers(Long familyId, List<Long> memberIds);
    
    Family removeMembers(Long familyId, List<Long> memberIds);
    
    List<Long> getFamilyMembers(Long familyId);
} 
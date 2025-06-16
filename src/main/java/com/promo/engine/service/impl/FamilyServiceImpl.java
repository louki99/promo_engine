package com.promo.engine.service.impl;

import com.promo.engine.dto.FamilyDTO;
import com.promo.engine.entity.Family;
import com.promo.engine.entity.FamilyMember;
import com.promo.engine.enums.FamilyType;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.FamilyMemberRepository;
import com.promo.engine.repository.FamilyRepository;
import com.promo.engine.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;

    @Override
    @Transactional
    public Family createFamily(FamilyDTO familyDTO) {
        Family family = new Family();
        family.setName(familyDTO.getName());
        family.setFamilyType(familyDTO.getFamilyType());
        family.setDescription(familyDTO.getDescription());
        return familyRepository.save(family);
    }

    @Override
    public List<Family> getAllFamilies() {
        return familyRepository.findAll();
    }

    @Override
    public Family getFamilyById(Long id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found"));
    }

    @Override
    public List<Family> getFamiliesByType(String familyType) {
        return familyRepository.findByFamilyType(FamilyType.valueOf(familyType));
    }

    @Override
    @Transactional
    public Family updateFamily(Long id, FamilyDTO familyDTO) {
        Family family = getFamilyById(id);
        family.setName(familyDTO.getName());
        family.setFamilyType(familyDTO.getFamilyType());
        family.setDescription(familyDTO.getDescription());
        return familyRepository.save(family);
    }

    @Override
    @Transactional
    public void deleteFamily(Long id) {
        Family family = getFamilyById(id);
        familyRepository.delete(family);
    }

    @Override
    @Transactional
    public Family addMembers(Long familyId, List<Long> memberIds) {
        Family family = getFamilyById(familyId);
        List<FamilyMember> members = memberIds.stream()
                .map(memberId -> {
                    FamilyMember member = new FamilyMember();
                    member.setFamily(family);
                    member.setMemberId(memberId);
                    return member;
                })
                .collect(Collectors.toList());
        familyMemberRepository.saveAll(members);
        return family;
    }

    @Override
    @Transactional
    public Family removeMembers(Long familyId, List<Long> memberIds) {
        Family family = getFamilyById(familyId);
        familyMemberRepository.deleteByFamilyIdAndMemberIdIn(familyId, memberIds);
        return family;
    }

    @Override
    public List<Long> getFamilyMembers(Long familyId) {
        return familyMemberRepository.findMemberIdsByFamilyId(familyId);
    }
}
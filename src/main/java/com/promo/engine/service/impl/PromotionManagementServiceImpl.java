package com.promo.engine.service.impl;

import com.promo.engine.dto.PromotionDTO;
import com.promo.engine.domain.Promotion;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.PromotionRepository;
import com.promo.engine.service.PromotionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionManagementServiceImpl implements PromotionManagementService {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public Promotion createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = new Promotion();
        updatePromotionFromDTO(promotion, promotionDTO);
        return promotionRepository.save(promotion);
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }

    @Override
    @Transactional
    public Promotion updatePromotion(Long id, PromotionDTO promotionDTO) {
        Promotion promotion = getPromotionById(id);
        updatePromotionFromDTO(promotion, promotionDTO);
        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotionRepository.delete(promotion);
    }

    @Override
    @Transactional
    public Promotion activatePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotion.setIsActive(true);
        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public Promotion deactivatePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotion.setIsActive(false);
        return promotionRepository.save(promotion);
    }

    private void updatePromotionFromDTO(Promotion promotion, PromotionDTO dto) {
        promotion.setPromoCode(dto.getPromoCode());
        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setIsActive(dto.isActive());
        promotion.setPriority(dto.getPriority());
        promotion.setExclusive(dto.isExclusive());
        promotion.setCombinabilityGroup(dto.getCombinabilityGroup());
        promotion.setStackingGroup(dto.getStackingGroup());
        promotion.setMaxStackCount(dto.getMaxStackCount());
        promotion.setMaxStackPerCustomer(dto.getMaxStackPerCustomer());
        promotion.setMaxStackPerOrder(dto.getMaxStackPerOrder());
    }
} 
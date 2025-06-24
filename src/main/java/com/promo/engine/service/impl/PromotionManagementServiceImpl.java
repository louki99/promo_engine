package com.promo.engine.service.impl;

import com.promo.engine.dto.PromotionDTO;
import com.promo.engine.domain.PromotionEntity;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionManagementServiceImpl {

    private final PromotionRepository promotionRepository;

    @Transactional
    public PromotionEntity createPromotion(PromotionDTO promotionDTO) {
        PromotionEntity promotion = new PromotionEntity();
        updatePromotionFromDTO(promotion, promotionDTO);
        return promotionRepository.save(promotion);
    }

    public List<PromotionEntity> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public PromotionEntity getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
    }

    @Transactional
    public PromotionEntity updatePromotion(Long id, PromotionDTO promotionDTO) {
        PromotionEntity promotion = getPromotionById(id);
        updatePromotionFromDTO(promotion, promotionDTO);
        return promotionRepository.save(promotion);
    }

    @Transactional
    public void deletePromotion(Long id) {
        PromotionEntity promotion = getPromotionById(id);
        promotionRepository.delete(promotion);
    }

    @Transactional
    public PromotionEntity activatePromotion(Long id) {
        PromotionEntity promotion = getPromotionById(id);
        promotion.setActive(true);
        return promotionRepository.save(promotion);
    }

    @Transactional
    public PromotionEntity deactivatePromotion(Long id) {
        PromotionEntity promotion = getPromotionById(id);
        promotion.setActive(false);
        return promotionRepository.save(promotion);
    }

    private void updatePromotionFromDTO(PromotionEntity promotion, PromotionDTO dto) {
        promotion.setPromoCode(dto.getPromoCode());
        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setActive(dto.isActive());
        promotion.setPriority(dto.getPriority());
        promotion.setExclusive(dto.isExclusive());
        promotion.setStackingGroup(dto.getStackingGroup());
        promotion.setMaxStackCount(dto.getMaxStackCount());
        promotion.setMaxStackPerCustomer(dto.getMaxStackPerCustomer());
        promotion.setMaxStackPerOrder(dto.getMaxStackPerOrder());
    }
}
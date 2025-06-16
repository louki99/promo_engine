package com.promo.engine.service.impl;

import com.promo.engine.dto.BundlePromotionDTO;
import com.promo.engine.entity.BundlePromotion;
import com.promo.engine.entity.Promotion;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.BundlePromotionRepository;
import com.promo.engine.repository.PromotionRepository;
import com.promo.engine.service.BundlePromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BundlePromotionServiceImpl implements BundlePromotionService {
    
    private final BundlePromotionRepository bundlePromotionRepository;
    private final PromotionRepository promotionRepository;
    
    @Override
    @Transactional
    public BundlePromotionDTO createBundlePromotion(BundlePromotionDTO bundlePromotionDTO) {
        Promotion promotion = promotionRepository.findById(bundlePromotionDTO.getPromotionId())
            .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
            
        BundlePromotion bundlePromotion = convertToEntity(bundlePromotionDTO);
        bundlePromotion.setPromotion(promotion);
        
        BundlePromotion savedBundle = bundlePromotionRepository.save(bundlePromotion);
        return convertToDTO(savedBundle);
    }
    
    @Override
    @Transactional
    public BundlePromotionDTO updateBundlePromotion(Long id, BundlePromotionDTO bundlePromotionDTO) {
        BundlePromotion existingBundle = bundlePromotionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bundle promotion not found"));
            
        updateEntityFromDTO(existingBundle, bundlePromotionDTO);
        
        BundlePromotion updatedBundle = bundlePromotionRepository.save(existingBundle);
        return convertToDTO(updatedBundle);
    }
    
    @Override
    @Transactional
    public void deleteBundlePromotion(Long id) {
        if (!bundlePromotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bundle promotion not found");
        }
        bundlePromotionRepository.deleteById(id);
    }
    
    @Override
    public BundlePromotionDTO getBundlePromotion(Long id) {
        BundlePromotion bundlePromotion = bundlePromotionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bundle promotion not found"));
        return convertToDTO(bundlePromotion);
    }
    
    @Override
    public List<BundlePromotionDTO> getAllBundlePromotions() {
        return bundlePromotionRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BundlePromotionDTO> getActiveBundlePromotions() {
        return bundlePromotionRepository.findByIsActiveTrue().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BundlePromotionDTO> getBundlePromotionsByPromotionId(Long promotionId) {
        return bundlePromotionRepository.findByPromotionId(promotionId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isBundleComplete(Long bundleId, List<BundlePromotion.BundleItem> cartItems) {
        BundlePromotion bundlePromotion = bundlePromotionRepository.findById(bundleId)
            .orElseThrow(() -> new ResourceNotFoundException("Bundle promotion not found"));
        return bundlePromotion.isBundleComplete(cartItems);
    }
    
    @Override
    public BundlePromotionDTO calculateBundleDiscount(Long bundleId, List<BundlePromotion.BundleItem> cartItems) {
        BundlePromotion bundlePromotion = bundlePromotionRepository.findById(bundleId)
            .orElseThrow(() -> new ResourceNotFoundException("Bundle promotion not found"));
            
        BundlePromotionDTO result = convertToDTO(bundlePromotion);
        result.setBundleDiscount(bundlePromotion.calculateBundleDiscount(cartItems));
        return result;
    }
    
    private BundlePromotion convertToEntity(BundlePromotionDTO dto) {
        BundlePromotion entity = new BundlePromotion();
        entity.setId(dto.getId());
        entity.setRequiredItems(dto.getRequiredItems().stream()
            .map(this::convertToBundleItem)
            .collect(Collectors.toList()));
        entity.setRewardItems(dto.getRewardItems().stream()
            .map(this::convertToBundleItem)
            .collect(Collectors.toList()));
        entity.setBundleDiscount(dto.getBundleDiscount());
        entity.setMaxBundles(dto.getMaxBundles());
        entity.setIsActive(dto.getIsActive());
        return entity;
    }
    
    private BundlePromotionDTO convertToDTO(BundlePromotion entity) {
        BundlePromotionDTO dto = new BundlePromotionDTO();
        dto.setId(entity.getId());
        dto.setRequiredItems(entity.getRequiredItems().stream()
            .map(this::convertToBundleItemDTO)
            .collect(Collectors.toList()));
        dto.setRewardItems(entity.getRewardItems().stream()
            .map(this::convertToBundleItemDTO)
            .collect(Collectors.toList()));
        dto.setBundleDiscount(entity.getBundleDiscount());
        dto.setMaxBundles(entity.getMaxBundles());
        dto.setIsActive(entity.getIsActive());
        dto.setPromotionId(entity.getPromotion().getId());
        return dto;
    }
    
    private BundlePromotion.BundleItem convertToBundleItem(BundlePromotionDTO.BundleItemDTO dto) {
        return new BundlePromotion.BundleItem(
            dto.getProductId(),
            dto.getQuantity(),
            dto.getMinQuantity(),
            dto.getMaxQuantity(),
            dto.getIsRequired()
        );
    }
    
    private BundlePromotionDTO.BundleItemDTO convertToBundleItemDTO(BundlePromotion.BundleItem entity) {
        return new BundlePromotionDTO.BundleItemDTO(
            entity.getProductId(),
            entity.getQuantity(),
            entity.getMinQuantity(),
            entity.getMaxQuantity(),
            entity.getIsRequired()
        );
    }
    
    private void updateEntityFromDTO(BundlePromotion entity, BundlePromotionDTO dto) {
        entity.setRequiredItems(dto.getRequiredItems().stream()
            .map(this::convertToBundleItem)
            .collect(Collectors.toList()));
        entity.setRewardItems(dto.getRewardItems().stream()
            .map(this::convertToBundleItem)
            .collect(Collectors.toList()));
        entity.setBundleDiscount(dto.getBundleDiscount());
        entity.setMaxBundles(dto.getMaxBundles());
        entity.setIsActive(dto.getIsActive());
    }
} 
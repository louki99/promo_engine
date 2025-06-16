package com.promo.engine.service.impl;

import com.promo.engine.entity.FlashSale;
import com.promo.engine.entity.FlashSaleInventory;
import com.promo.engine.exception.ResourceNotFoundException;
import com.promo.engine.repository.FlashSaleRepository;
import com.promo.engine.service.CustomerHistoryService;
import com.promo.engine.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlashSaleServiceImpl implements FlashSaleService {
    
    private final FlashSaleRepository flashSaleRepository;
    private final CustomerHistoryService customerHistoryService;
    
    @Override
    @Transactional
    public FlashSale createFlashSale(FlashSale flashSale) {
        return flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void updateFlashSale(FlashSale flashSale) {
        if (!flashSaleRepository.existsById(flashSale.getId())) {
            throw new ResourceNotFoundException("Flash sale not found");
        }
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void deleteFlashSale(Long id) {
        FlashSale flashSale = getFlashSale(id);
        flashSale.updateStatus(FlashSale.Status.CANCELLED);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    public FlashSale getFlashSale(Long id) {
        return flashSaleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flash sale not found"));
    }
    
    @Override
    public List<FlashSale> getActiveFlashSales() {
        return flashSaleRepository.findAll().stream()
                .filter(FlashSale::isActive)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<FlashSale> getUpcomingFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findAll().stream()
                .filter(sale -> sale.getStartTime().isAfter(now) && 
                              sale.getStatus() == FlashSale.Status.SCHEDULED)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void reserveInventory(Long flashSaleId, Long productId, int quantity) {
        FlashSaleInventory inventory = getInventory(flashSaleId, productId);
        inventory.reserveQuantity(quantity);
        flashSaleRepository.save(inventory.getFlashSale());
    }
    
    @Override
    @Transactional
    public void releaseInventory(Long flashSaleId, Long productId, int quantity) {
        FlashSaleInventory inventory = getInventory(flashSaleId, productId);
        inventory.releaseQuantity(quantity);
        flashSaleRepository.save(inventory.getFlashSale());
    }
    
    @Override
    public int getAvailableInventory(Long flashSaleId, Long productId) {
        return getInventory(flashSaleId, productId).getAvailableQuantity();
    }
    
    @Override
    public Map<Long, Integer> getAvailableInventoryForProducts(Long flashSaleId, List<Long> productIds) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        return flashSale.getInventory().stream()
                .filter(inv -> productIds.contains(inv.getProductId()))
                .collect(Collectors.toMap(
                        FlashSaleInventory::getProductId,
                        FlashSaleInventory::getAvailableQuantity
                ));
    }
    
    @Override
    public BigDecimal calculateFlashSalePrice(Long flashSaleId, Long productId, int quantity) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        FlashSaleInventory inventory = getInventory(flashSaleId, productId);
        
        if (!inventory.isWithinPurchaseLimits(quantity)) {
            throw new IllegalArgumentException("Quantity is outside purchase limits");
        }
        
        BigDecimal basePrice = inventory.getFlashSalePrice();
        
        // Apply early bird discount if applicable
        if (flashSale.isEarlyBird()) {
            basePrice = basePrice.subtract(flashSale.getApplicableDiscount());
        }
        
        // Apply loyalty multiplier
        String loyaltyTier = customerHistoryService.getLoyaltyTier(flashSaleId);
        double loyaltyMultiplier = flashSale.getLoyaltyMultiplier(loyaltyTier);
        basePrice = basePrice.multiply(BigDecimal.valueOf(loyaltyMultiplier));
        
        // Apply bulk discount if applicable
        BigDecimal bulkDiscount = flashSale.getBulkDiscount(quantity);
        basePrice = basePrice.subtract(bulkDiscount);
        
        // Apply demand factor
        double demandFactor = flashSale.getDemandFactor(productId);
        basePrice = basePrice.multiply(BigDecimal.valueOf(demandFactor));
        
        return basePrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    @Override
    @Transactional
    public void updateDemandFactor(Long flashSaleId, Long productId, double factor) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.getDemandFactors().put(productId, factor);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void applyEarlyBirdDiscount(Long flashSaleId, BigDecimal discount) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.setEarlyBirdDiscount(discount);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void applyLoyaltyMultiplier(Long flashSaleId, Long customerId, double multiplier) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        String loyaltyTier = customerHistoryService.getLoyaltyTier(customerId);
        flashSale.getLoyaltyMultipliers().put(loyaltyTier, multiplier);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void applyBulkPurchaseDiscount(Long flashSaleId, int minQuantity, BigDecimal discount) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.getBulkDiscounts().put(minQuantity, discount);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void updateInventoryLevels(Long flashSaleId, Map<Long, Integer> inventoryUpdates) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        for (Map.Entry<Long, Integer> update : inventoryUpdates.entrySet()) {
            FlashSaleInventory inventory = getInventory(flashSaleId, update.getKey());
            inventory.setTotalQuantity(update.getValue());
        }
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void startFlashSale(Long flashSaleId) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.updateStatus(FlashSale.Status.ACTIVE);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void endFlashSale(Long flashSaleId) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.updateStatus(FlashSale.Status.ENDED);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    public boolean isFlashSaleActive(Long flashSaleId) {
        return getFlashSale(flashSaleId).isActive();
    }
    
    @Override
    @Transactional
    public void extendFlashSale(Long flashSaleId, int additionalMinutes) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.extendDuration(additionalMinutes);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void pauseFlashSale(Long flashSaleId) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.updateStatus(FlashSale.Status.PAUSED);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    @Transactional
    public void resumeFlashSale(Long flashSaleId) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.updateStatus(FlashSale.Status.ACTIVE);
        flashSaleRepository.save(flashSale);
    }
    
    @Override
    public Map<Long, FlashSaleInventory> getFlashSaleInventory(Long flashSaleId) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        return flashSale.getInventory().stream()
                .collect(Collectors.toMap(
                        FlashSaleInventory::getProductId,
                        inventory -> inventory
                ));
    }
    
    @Override
    @Transactional
    public void updateFlashSaleStatus(Long flashSaleId, FlashSale.Status status) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        flashSale.updateStatus(status);
        flashSaleRepository.save(flashSale);
    }
    
    private FlashSaleInventory getInventory(Long flashSaleId, Long productId) {
        FlashSale flashSale = getFlashSale(flashSaleId);
        return flashSale.getInventory().stream()
                .filter(inv -> inv.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product"));
    }
} 
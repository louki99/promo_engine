package com.promo.engine.service;

import com.promo.engine.entity.FlashSale;
import com.promo.engine.entity.FlashSaleInventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FlashSaleService {
    FlashSale createFlashSale(FlashSale flashSale);
    
    void updateFlashSale(FlashSale flashSale);
    
    void deleteFlashSale(Long id);
    
    FlashSale getFlashSale(Long id);
    
    List<FlashSale> getActiveFlashSales();
    
    List<FlashSale> getUpcomingFlashSales();
    
    void reserveInventory(Long flashSaleId, Long productId, int quantity);
    
    void releaseInventory(Long flashSaleId, Long productId, int quantity);
    
    int getAvailableInventory(Long flashSaleId, Long productId);
    
    Map<Long, Integer> getAvailableInventoryForProducts(Long flashSaleId, List<Long> productIds);
    
    BigDecimal calculateFlashSalePrice(Long flashSaleId, Long productId, int quantity);
    
    void updateDemandFactor(Long flashSaleId, Long productId, double factor);
    
    void applyEarlyBirdDiscount(Long flashSaleId, BigDecimal discount);
    
    void applyLoyaltyMultiplier(Long flashSaleId, Long customerId, double multiplier);
    
    void applyBulkPurchaseDiscount(Long flashSaleId, int minQuantity, BigDecimal discount);
    
    void updateInventoryLevels(Long flashSaleId, Map<Long, Integer> inventoryUpdates);
    
    void startFlashSale(Long flashSaleId);
    
    void endFlashSale(Long flashSaleId);
    
    boolean isFlashSaleActive(Long flashSaleId);
    
    void extendFlashSale(Long flashSaleId, int additionalMinutes);
    
    void pauseFlashSale(Long flashSaleId);
    
    void resumeFlashSale(Long flashSaleId);
    
    Map<Long, FlashSaleInventory> getFlashSaleInventory(Long flashSaleId);
    
    void updateFlashSaleStatus(Long flashSaleId, FlashSale.Status status);
} 
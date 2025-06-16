package com.promo.engine.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "flash_sale_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;
    
    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0;
    
    @Column(name = "sold_quantity")
    private Integer soldQuantity = 0;
    
    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;
    
    @Column(name = "flash_sale_price", nullable = false)
    private BigDecimal flashSalePrice;
    
    @Column(name = "min_purchase_quantity")
    private Integer minPurchaseQuantity = 1;
    
    @Column(name = "max_purchase_quantity")
    private Integer maxPurchaseQuantity;
    
    public int getAvailableQuantity() {
        return totalQuantity - reservedQuantity - soldQuantity;
    }
    
    public boolean hasAvailableQuantity(int requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }
    
    public void reserveQuantity(int quantity) {
        if (!hasAvailableQuantity(quantity)) {
            throw new IllegalStateException("Insufficient inventory available");
        }
        this.reservedQuantity += quantity;
    }
    
    public void releaseQuantity(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException("Cannot release more than reserved quantity");
        }
        this.reservedQuantity -= quantity;
    }
    
    public void markAsSold(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException("Cannot sell more than reserved quantity");
        }
        this.reservedQuantity -= quantity;
        this.soldQuantity += quantity;
    }
    
    public boolean isWithinPurchaseLimits(int quantity) {
        return quantity >= minPurchaseQuantity && 
               (maxPurchaseQuantity == null || quantity <= maxPurchaseQuantity);
    }
} 
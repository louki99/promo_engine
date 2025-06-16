package com.promo.engine.event.product;


import com.promo.engine.domain.PromoProduct;
import com.promo.engine.event.product.data.ProductDeletedEvent;
import com.promo.engine.event.product.data.ProductUpdatedEvent;
import com.promo.engine.repository.PromoProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final PromoProductRepository promoProductRepository;

    @KafkaListener (topics = "${kafka.topics.product-updates}")
    public void handleProductUpdated(ProductUpdatedEvent event) {
        PromoProduct product = promoProductRepository.findById(event.getProductId())
                .orElse(new PromoProduct());
        product.setProductId(event.getProductId());
        product.setName(event.getName());
        product.setPrice(event.getPrice());
        product.setFamilyId(event.getFamilyId());
        product.setFamilyName(event.getFamilyName());
        product.setSkuPoints(event.getSkuPoints());
        product.setCategoryId(event.getCategoryId());
        product.setCategoryName(event.getCategoryName());
        product.setIsActive(event.getIsActive());
        product.setLastUpdated(LocalDateTime.now());
        promoProductRepository.save(product);
        log.info("Updated local product cache for productId: {}", event.getProductId());
    }

    @KafkaListener(topics = "${kafka.topics.product-deletes}")
    public void handleProductDeleted(ProductDeletedEvent event) {
        promoProductRepository.deleteById(event.getProductId());
        log.info("Deleted local product cache for productId: {}", event.getProductId());
    }
}
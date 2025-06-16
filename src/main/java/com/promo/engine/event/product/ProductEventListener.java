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
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final PromoProductRepository promoProductRepository;

    @KafkaListener(topics = "${kafka.topics.product-updates}", groupId = "${kafka.consumer.group-id}")
    public void handleProductUpdated(ProductUpdatedEvent event) {
        try {
            Optional<PromoProduct> optionalProduct = promoProductRepository.findById(event.getProductId());

            if (optionalProduct.isPresent()) {
                PromoProduct existing = optionalProduct.get();

                // Ignore stale updates
                if (event.getUpdatedAt() != null &&
                        existing.getLastUpdated() != null &&
                        event.getUpdatedAt().isBefore(existing.getLastUpdated())) {
                    log.warn("Ignoring stale update for product {}", event.getProductId());
                    return;
                }

                updateProductFields(existing, event);
                log.info("Updated existing product with ID: {}", event.getProductId());
                promoProductRepository.save(existing);
            } else {
                PromoProduct newProduct = new PromoProduct();
                updateProductFields(newProduct, event);
                log.info("Created new product with ID: {}", event.getProductId());
                promoProductRepository.save(newProduct);
            }
        } catch (Exception e) {
            log.error("Error processing product update event", e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.product-deletes}", groupId = "${kafka.consumer.group-id}")
    public void handleProductDeleted(ProductDeletedEvent event) {
        promoProductRepository.deleteById(event.getProductId());
        log.info("Deleted local product cache for productId: {}", event.getProductId());
    }

    private void updateProductFields(PromoProduct product, ProductUpdatedEvent event) {
        product.setProductId(event.getProductId());
        product.setName(event.getName());
        product.setPrice(event.getPrice());
        product.setFamilyId(event.getFamilyId());
        product.setFamilyName(event.getFamilyName());
        product.setSkuPoints(event.getSkuPoints());
        product.setCategoryId(event.getCategoryId());
        product.setCategoryName(event.getCategoryName());
        product.setIsActive(event.getIsActive());
        product.setLastUpdated(event.getUpdatedAt() != null ? event.getUpdatedAt() : LocalDateTime.now());
    }
}

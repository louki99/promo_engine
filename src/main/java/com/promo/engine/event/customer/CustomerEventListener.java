package com.promo.engine.event.customer;

import com.promo.engine.domain.PromoCustomer;
import com.promo.engine.event.customer.data.CustomerDeletedEvent;
import com.promo.engine.event.customer.data.CustomerUpdatedEvent;
import com.promo.engine.repository.PromoCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {

    private final PromoCustomerRepository promoCustomerRepository;

    @KafkaListener(topics = "${kafka.topic.customer-updates}", groupId = "${kafka.consumer.group-id}")
    public void handleCustomerUpdated(CustomerUpdatedEvent event) {
        try {
            Optional<PromoCustomer> optionalCustomer = promoCustomerRepository.findById(event.getCustomerId());
            PromoCustomer customer = optionalCustomer.orElseGet(PromoCustomer::new);

            // Idempotency check
            if (optionalCustomer.isPresent() && customer.getLastUpdated() != null
                    && event.getUpdatedAt() != null
                    && event.getUpdatedAt().isBefore(customer.getLastUpdated())) {
                log.warn("Ignoring stale customer update event for customerId: {}", event.getCustomerId());
                return;
            }

            customer.setCustomerId(event.getCustomerId());
            customer.setFamilyIds(event.getFamilyIds());
            customer.setSegmentIds(event.getSegmentIds());
            customer.setLoyaltyTier(event.getLoyaltyTier());
            customer.setLoyaltyPoints(event.getLoyaltyPoints());
            customer.setTotalSpend(event.getTotalSpend());
            customer.setLastUpdated(event.getUpdatedAt() != null ? event.getUpdatedAt() : LocalDateTime.now());

            promoCustomerRepository.save(customer);
            log.info("Updated local customer cache for customerId: {}", event.getCustomerId());
        } catch (Exception e) {
            log.error("Error updating customer for customerId: {}", event.getCustomerId(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topic.customer-deletes}", groupId = "${kafka.consumer.group-id}")
    public void handleCustomerDeleted(CustomerDeletedEvent event) {
        try {
            promoCustomerRepository.deleteById(event.getCustomerId());
            log.info("Deleted local customer cache for customerId: {}", event.getCustomerId());
        } catch (Exception e) {
            log.error("Error deleting customer for customerId: {}", event.getCustomerId(), e);
        }
    }
}
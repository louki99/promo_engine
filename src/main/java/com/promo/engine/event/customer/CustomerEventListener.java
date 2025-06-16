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

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventListener {
    private final PromoCustomerRepository promoCustomerRepository;

    @KafkaListener (topics = "customer-updates", groupId = "promo-service")
    public void handleCustomerUpdated(CustomerUpdatedEvent event) {
        PromoCustomer customer = promoCustomerRepository.findById(event.getCustomerId())
                .orElse(new PromoCustomer());
        customer.setCustomerId(event.getCustomerId());
        customer.setFamilyIds(event.getFamilyIds());
        customer.setSegmentIds(event.getSegmentIds());
        customer.setLoyaltyTier(event.getLoyaltyTier());
        customer.setLoyaltyPoints(event.getLoyaltyPoints());
        customer.setTotalSpend(event.getTotalSpend());
        customer.setLastUpdated(LocalDateTime.now());
        promoCustomerRepository.save(customer);
        log.info("Updated local customer cache for customerId: {}",event.getCustomerId());
    }

    @KafkaListener(topics = "customer-deletes", groupId = "promo-service")
    public void handleCustomerDeleted(CustomerDeletedEvent event) {
        promoCustomerRepository.deleteById(event.getCustomerId());
        log.info("Deleted local customer cache for customerId: {}",event.getCustomerId());
    }
}

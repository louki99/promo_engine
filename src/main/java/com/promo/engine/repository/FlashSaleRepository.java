package com.promo.engine.repository;

import com.promo.engine.domain.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {
    List<FlashSale> findByStatus(FlashSale.Status status);
    List<FlashSale> findByStartTimeAfterAndStatus(LocalDateTime time, FlashSale.Status status);
    List<FlashSale> findByEndTimeBeforeAndStatus(LocalDateTime time, FlashSale.Status status);
    List<FlashSale> findByIsActiveTrue();
}
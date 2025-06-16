package com.promo.engine.repository;

import com.promo.engine.entity.CustomerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerHistoryRepository extends JpaRepository<CustomerHistory, Long> {
    Optional<CustomerHistory> findByCustomerId(Long customerId);
}
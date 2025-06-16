package com.promo.engine.repository;

import com.promo.engine.entity.CustomerSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerSegmentRepository extends JpaRepository<CustomerSegment, Long> {
    List<CustomerSegment> findByIsActiveTrue();
} 
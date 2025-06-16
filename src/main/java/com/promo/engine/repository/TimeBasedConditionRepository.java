package com.promo.engine.repository;

import com.promo.engine.domain.TimeBasedCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeBasedConditionRepository extends JpaRepository<TimeBasedCondition, Long> {
    List<TimeBasedCondition> findByIsRecurringTrue();
} 
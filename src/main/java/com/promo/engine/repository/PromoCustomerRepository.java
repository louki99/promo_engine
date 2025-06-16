package com.promo.engine.repository;

import com.promo.engine.domain.PromoCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCustomerRepository extends JpaRepository<PromoCustomer, Long>{

    @Query ("SELECT c FROM PromoCustomer c WHERE c.customerId IN :customerIds")
    List<PromoCustomer> findAllByIds(@Param ("customerIds") List<Long> customerIds);

    @Query("SELECT c FROM PromoCustomer c WHERE :familyId MEMBER OF c.familyIds")
    List<PromoCustomer> findAllByFamilyId(@Param("familyId") Long familyId);

    @Query("SELECT c FROM PromoCustomer c WHERE :segmentId MEMBER OF c.segmentIds")
    List<PromoCustomer> findAllBySegmentId(@Param("segmentId") Long segmentId);

    Optional<PromoCustomer> findByCustomerId(Long customerId);
}
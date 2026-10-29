package com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilityBillRepository extends JpaRepository<UtilityBill, Long> {

    List<UtilityBill> findByUnitId(Long unitId);

    Optional<UtilityBill> findByUnitIdAndBillingPeriod_StartDate(Long unitId, LocalDate startDate);

    /** A unit has at most one bill per period. */
    boolean existsByUnitIdAndBillingPeriod_StartDate(Long unitId, LocalDate startDate);
}
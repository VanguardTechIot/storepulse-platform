package com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UtilityBillRepository extends JpaRepository<UtilityBill, Long> {

    /** A unit has at most one bill per period. */
    boolean existsByUnitIdAndPeriod_StartDate(Long unitId, LocalDate startDate);
}
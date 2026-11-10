package com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityTariff;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UtilityTariffRepository extends JpaRepository<UtilityTariff, Long> {

    /** The newest tariff of the utility that is already in force on the given date. */
    Optional<UtilityTariff> findFirstByUtilityTypeAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            UtilityType utilityType, LocalDate date);
}
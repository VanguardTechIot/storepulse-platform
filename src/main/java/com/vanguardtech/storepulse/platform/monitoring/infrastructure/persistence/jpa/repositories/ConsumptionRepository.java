package com.vanguardtech.storepulse.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
    Optional<Consumption> findByResourceIdAndMeasurementTypeAndPeriodStart(
            Long resourceId, MeasurementType measurementType, Instant periodStart);

    List<Consumption> findByResourceIdOrderByPeriodStartAsc(Long resourceId);

    List<Consumption> findAllByOrderByPeriodStartAsc();
}

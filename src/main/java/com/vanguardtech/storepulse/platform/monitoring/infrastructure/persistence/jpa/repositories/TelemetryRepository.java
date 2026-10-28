package com.vanguardtech.storepulse.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {
    @EntityGraph(attributePaths = "measurements")
    List<Telemetry> findAllByOrderByReceivedAtDesc();

    @EntityGraph(attributePaths = "measurements")
    List<Telemetry> findByDeviceIdOrderByReceivedAtDesc(Long deviceId);

    @EntityGraph(attributePaths = "measurements")
    Optional<Telemetry> findWithMeasurementsById(Long id);
}

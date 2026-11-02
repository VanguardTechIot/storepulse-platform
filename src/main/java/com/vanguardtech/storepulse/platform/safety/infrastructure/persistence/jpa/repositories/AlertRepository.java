package com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.Alert;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.AlertStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByOrderByDetectedAtDesc();

    boolean existsByDeviceIdAndStatusNot(Long deviceId, AlertStatus status);

    boolean existsByDeviceIdAndSensorTypeAndStatusNot(Long deviceId, SensorType sensorType, AlertStatus status);
}

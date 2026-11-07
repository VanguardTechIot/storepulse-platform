package com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceStateRepository extends JpaRepository<DeviceState, Long> {
    Optional<DeviceState> findByDeviceId(Long deviceId);
}

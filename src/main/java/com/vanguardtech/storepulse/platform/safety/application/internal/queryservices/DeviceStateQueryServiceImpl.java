package com.vanguardtech.storepulse.platform.safety.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.DeviceState;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetDeviceStateByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.safety.domain.services.DeviceStateQueryService;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.DeviceStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceStateQueryServiceImpl implements DeviceStateQueryService {

    private final DeviceStateRepository deviceStateRepository;

    @Override
    public Optional<DeviceState> handle(GetDeviceStateByDeviceIdQuery query) {
        return deviceStateRepository.findByDeviceId(query.deviceId());
    }
}

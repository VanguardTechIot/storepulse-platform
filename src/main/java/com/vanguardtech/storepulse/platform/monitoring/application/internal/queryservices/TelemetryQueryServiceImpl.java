package com.vanguardtech.storepulse.platform.monitoring.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetAllTelemetryQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetTelemetryByIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.TelemetryQueryService;
import com.vanguardtech.storepulse.platform.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TelemetryQueryServiceImpl implements TelemetryQueryService {

    private final TelemetryRepository telemetryRepository;

    public TelemetryQueryServiceImpl(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    @Override
    public List<Telemetry> handle(GetAllTelemetryQuery query) {
        return telemetryRepository.findAllByOrderByReceivedAtDesc();
    }

    @Override
    public List<Telemetry> handle(GetTelemetryByDeviceIdQuery query) {
        return telemetryRepository.findByDeviceIdOrderByReceivedAtDesc(query.deviceId());
    }

    @Override
    public Optional<Telemetry> handle(GetTelemetryByIdQuery query) {
        return telemetryRepository.findWithMeasurementsById(query.telemetryId());
    }
}

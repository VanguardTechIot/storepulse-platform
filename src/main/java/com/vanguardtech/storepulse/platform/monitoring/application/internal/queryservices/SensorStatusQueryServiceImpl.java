package com.vanguardtech.storepulse.platform.monitoring.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.entities.Measurement;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdsQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.SensorStatus;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.SensorStatusQueryService;
import com.vanguardtech.storepulse.platform.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SensorStatusQueryServiceImpl implements SensorStatusQueryService {

    private final TelemetryRepository telemetryRepository;

    public SensorStatusQueryServiceImpl(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    @Override
    public List<SensorStatus> handle(GetSensorStatusByDeviceIdQuery query) {
        return latestOf(telemetryRepository.findByDeviceIdOrderByReceivedAtDesc(query.deviceId()));
    }

    @Override
    public List<SensorStatus> handle(GetSensorStatusByDeviceIdsQuery query) {
        if (query.deviceIds().isEmpty()) return List.of();
        return latestOf(telemetryRepository.findByDeviceIdInOrderByReceivedAtDesc(query.deviceIds()));
    }

    private List<SensorStatus> latestOf(List<Telemetry> history) {
        var latest = new EnumMap<MeasurementType, Measurement>(MeasurementType.class);
        history.stream()
                .flatMap(telemetry -> telemetry.getMeasurements().stream())
                .forEach(measurement -> latest.merge(measurement.getType(), measurement,
                        (current, candidate) -> candidate.getRecordedAt().isAfter(current.getRecordedAt()) ? candidate : current));
        return latest.values().stream()
                .map(measurement -> new SensorStatus(
                        measurement.getType(), measurement.getValue().value(), measurement.getRecordedAt()))
                .toList();
    }
}

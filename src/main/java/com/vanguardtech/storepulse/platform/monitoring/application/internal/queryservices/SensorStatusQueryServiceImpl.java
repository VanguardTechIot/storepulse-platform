package com.vanguardtech.storepulse.platform.monitoring.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.entities.Measurement;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdQuery;
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
        var latest = new EnumMap<MeasurementType, Measurement>(MeasurementType.class);
        telemetryRepository.findByDeviceIdOrderByReceivedAtDesc(query.deviceId()).stream()
                .flatMap(telemetry -> telemetry.getMeasurements().stream())
                .forEach(measurement -> latest.merge(measurement.getType(), measurement,
                        (current, candidate) -> candidate.getRecordedAt().isAfter(current.getRecordedAt()) ? candidate : current));
        return latest.values().stream()
                .map(measurement -> new SensorStatus(
                        measurement.getType(), measurement.getValue().value(), measurement.getRecordedAt()))
                .toList();
    }
}

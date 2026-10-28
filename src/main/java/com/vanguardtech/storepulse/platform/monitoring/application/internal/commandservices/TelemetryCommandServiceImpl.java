package com.vanguardtech.storepulse.platform.monitoring.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalResourceAssetService;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Consumption;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.commands.RegisterTelemetryCommand;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.entities.Measurement;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementValue;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.TelemetryCommandService;
import com.vanguardtech.storepulse.platform.monitoring.infrastructure.persistence.jpa.repositories.ConsumptionRepository;
import com.vanguardtech.storepulse.platform.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelemetryCommandServiceImpl implements TelemetryCommandService {

    private final TelemetryRepository telemetryRepository;
    private final ConsumptionRepository consumptionRepository;
    private final ExternalResourceAssetService externalResourceAssetService;

    public TelemetryCommandServiceImpl(TelemetryRepository telemetryRepository,
                                       ConsumptionRepository consumptionRepository,
                                       ExternalResourceAssetService externalResourceAssetService) {
        this.telemetryRepository = telemetryRepository;
        this.consumptionRepository = consumptionRepository;
        this.externalResourceAssetService = externalResourceAssetService;
    }

    @Override
    @Transactional
    public Telemetry handle(RegisterTelemetryCommand command) {
        var telemetry = new Telemetry(command.deviceId(), command.receivedAt());
        command.measurements().forEach(measurement -> telemetry.addMeasurement(
                measurement.type(), new MeasurementValue(measurement.value()), measurement.recordedAt()));
        var saved = telemetryRepository.save(telemetry);
        externalResourceAssetService.fetchResourceIdByDeviceId(command.deviceId())
                .ifPresent(resourceId -> accumulateConsumption(resourceId, saved));
        return saved;
    }

    private void accumulateConsumption(Long resourceId, Telemetry telemetry) {
        telemetry.getMeasurements().stream()
                .filter(measurement -> measurement.getType().isConsumption())
                .forEach(measurement -> addToPeriod(resourceId, measurement));
    }

    private void addToPeriod(Long resourceId, Measurement measurement) {
        var period = Consumption.periodOf(measurement.getRecordedAt());
        var consumption = consumptionRepository
                .findByResourceIdAndMeasurementTypeAndPeriodStart(resourceId, measurement.getType(), Consumption.startOf(period))
                .orElseGet(() -> new Consumption(resourceId, measurement.getType(), period));
        consumption.addReading(measurement.getValue().value());
        consumptionRepository.save(consumption);
    }
}

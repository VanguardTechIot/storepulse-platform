package com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.entities.Measurement;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.events.MeasurementRecordedEvent;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.events.MotionDetectedEvent;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.events.SmokeDetectedEvent;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.events.TelemetryReceivedEvent;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.events.UtilityConsumptionRecordedEvent;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementValue;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "telemetry")
@Getter
public class Telemetry extends AuditableAbstractAggregateRoot<Telemetry> {

    @Column(nullable = false)
    private Long deviceId;

    @Column(nullable = false)
    private Instant receivedAt;

    @OneToMany(mappedBy = "telemetry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Measurement> measurements = new ArrayList<>();

    protected Telemetry() {
    }

    public Telemetry(Long deviceId, Instant receivedAt) {
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
        this.deviceId = deviceId;
        this.receivedAt = receivedAt != null ? receivedAt : Instant.now();
        registerEvent(new TelemetryReceivedEvent(deviceId, this.receivedAt));
    }

    public Measurement addMeasurement(MeasurementType type, MeasurementValue value, Instant recordedAt) {
        var measurement = new Measurement(this, type, value, recordedAt);
        measurements.add(measurement);
        var occurredOn = measurement.getRecordedAt();
        registerEvent(new MeasurementRecordedEvent(deviceId, type, value.value(), occurredOn));
        if (measurement.isMotionDetected())
            registerEvent(new MotionDetectedEvent(deviceId, occurredOn));
        if (measurement.isSmokeDetected())
            registerEvent(new SmokeDetectedEvent(deviceId, value.value(), occurredOn));
        if (type.isConsumption())
            registerEvent(new UtilityConsumptionRecordedEvent(deviceId, type, value.value(), occurredOn));
        return measurement;
    }

    public List<Measurement> getMeasurements() {
        return Collections.unmodifiableList(measurements);
    }

    public boolean hasMeasurements() {
        return !measurements.isEmpty();
    }
}

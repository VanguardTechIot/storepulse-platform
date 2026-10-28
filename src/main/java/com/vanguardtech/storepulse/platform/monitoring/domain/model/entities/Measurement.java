package com.vanguardtech.storepulse.platform.monitoring.domain.model.entities;

import com.vanguardtech.storepulse.platform.monitoring.domain.model.aggregates.Telemetry;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementType;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.MeasurementValue;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "measurements")
@Getter
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "telemetry_id")
    private Telemetry telemetry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementType type;

    @Embedded
    private MeasurementValue value;

    @Column(nullable = false)
    private Instant recordedAt;

    protected Measurement() {
    }

    public Measurement(Telemetry telemetry, MeasurementType type, MeasurementValue value, Instant recordedAt) {
        if (telemetry == null)
            throw new IllegalArgumentException("Telemetry is required");
        if (type == null)
            throw new IllegalArgumentException("Measurement type is required");
        if (value == null)
            throw new IllegalArgumentException("Measurement value is required");
        if (type == MeasurementType.MOTION && value.value() != 0 && value.value() != 1)
            throw new IllegalArgumentException("Motion value must be 0 or 1");
        if ((type.isConsumption() || type == MeasurementType.SMOKE) && value.value() < 0)
            throw new IllegalArgumentException(type + " value cannot be negative");
        this.telemetry = telemetry;
        this.type = type;
        this.value = value;
        this.recordedAt = recordedAt != null ? recordedAt : telemetry.getReceivedAt();
    }

    public boolean isMotionDetected() {
        return type == MeasurementType.MOTION && value.isPositive();
    }

    public boolean isSmokeDetected() {
        return type == MeasurementType.SMOKE && value.isPositive();
    }
}

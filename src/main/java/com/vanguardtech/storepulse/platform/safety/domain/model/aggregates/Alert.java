package com.vanguardtech.storepulse.platform.safety.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.safety.domain.exceptions.SafetyError;
import com.vanguardtech.storepulse.platform.safety.domain.model.events.AlertAcknowledgedEvent;
import com.vanguardtech.storepulse.platform.safety.domain.model.events.AlertResolvedEvent;
import com.vanguardtech.storepulse.platform.safety.domain.model.events.AlertTriggeredEvent;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.AlertStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "alerts")
@Getter
public class Alert extends AuditableAbstractAggregateRoot<Alert> {

    @Column(nullable = false)
    private Long safetyRuleId;

    @Column(nullable = false)
    private Long deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType sensorType;

    @Column(nullable = false)
    private Double measuredValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    @Column(nullable = false)
    private Instant detectedAt;

    private Instant acknowledgedAt;

    private Instant resolvedAt;

    private String resolutionNote;

    protected Alert() {
    }

    public Alert(Long safetyRuleId, Long deviceId, SensorType sensorType, Double measuredValue, Instant detectedAt) {
        if (safetyRuleId == null || safetyRuleId <= 0)
            throw new IllegalArgumentException("Safety rule ID must be a positive number");
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
        if (sensorType == null)
            throw new IllegalArgumentException("Sensor type is required");
        if (measuredValue == null)
            throw new IllegalArgumentException("Measured value is required");
        this.safetyRuleId = safetyRuleId;
        this.deviceId = deviceId;
        this.sensorType = sensorType;
        this.measuredValue = measuredValue;
        this.status = AlertStatus.ACTIVE;
        this.detectedAt = detectedAt != null ? detectedAt : Instant.now();
        registerEvent(new AlertTriggeredEvent(deviceId, safetyRuleId, measuredValue, this.detectedAt));
    }

    public void acknowledge() {
        if (status != AlertStatus.ACTIVE)
            throw new BusinessRuleException(SafetyError.ALERT_CANNOT_BE_ACKNOWLEDGED);
        this.status = AlertStatus.ACKNOWLEDGED;
        this.acknowledgedAt = Instant.now();
        registerEvent(new AlertAcknowledgedEvent(getId(), this.acknowledgedAt));
    }

    public void resolve(String resolutionNote) {
        if (status != AlertStatus.ACKNOWLEDGED)
            throw new BusinessRuleException(SafetyError.ALERT_CANNOT_BE_RESOLVED);
        if (resolutionNote == null || resolutionNote.isBlank())
            throw new IllegalArgumentException("Resolution note is required");
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = Instant.now();
        this.resolutionNote = resolutionNote.trim();
        registerEvent(new AlertResolvedEvent(getId(), this.resolvedAt));
    }
}

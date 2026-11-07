package com.vanguardtech.storepulse.platform.safety.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.BusinessHours;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.DeviceStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.ResponseAction;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.Threshold;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "safety_rules")
@Getter
public class SafetyRule extends AuditableAbstractAggregateRoot<SafetyRule> {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType sensorType;

    @Embedded
    private Threshold threshold;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus deviceStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResponseAction responseAction;

    @Column(nullable = false)
    private boolean enabled;

    protected SafetyRule() {
    }

    public SafetyRule(String name, SensorType sensorType, Threshold threshold,
                      DeviceStatus deviceStatus, ResponseAction responseAction) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Safety rule name is required");
        if (sensorType == null)
            throw new IllegalArgumentException("Sensor type is required");
        if (threshold == null)
            throw new IllegalArgumentException("Threshold is required");
        if (deviceStatus == null)
            throw new IllegalArgumentException("Device status is required");
        if (responseAction == null)
            throw new IllegalArgumentException("Response action is required");
        this.name = name;
        this.sensorType = sensorType;
        this.threshold = threshold;
        this.deviceStatus = deviceStatus;
        this.responseAction = responseAction;
        this.enabled = true;
    }

    public boolean isTriggeredBy(Double measuredValue, Instant recordedAt) {
        if (!enabled || !threshold.isMetBy(measuredValue))
            return false;
        return sensorType != SensorType.MOTION || !BusinessHours.includes(recordedAt);
    }
}

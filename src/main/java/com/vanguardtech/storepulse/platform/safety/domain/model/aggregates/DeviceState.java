package com.vanguardtech.storepulse.platform.safety.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.DeviceStatus;
import com.vanguardtech.storepulse.platform.safety.domain.model.valueobjects.ResponseAction;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "device_states")
@Getter
public class DeviceState extends AuditableAbstractAggregateRoot<DeviceState> {

    @Column(nullable = false, unique = true)
    private Long deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResponseAction response;

    @Column(nullable = false)
    private Instant evaluatedAt;

    protected DeviceState() {
    }

    public DeviceState(Long deviceId, DeviceStatus status, ResponseAction response, Instant evaluatedAt) {
        if (deviceId == null || deviceId <= 0)
            throw new IllegalArgumentException("Device ID must be a positive number");
        if (status == null)
            throw new IllegalArgumentException("Device status is required");
        if (response == null)
            throw new IllegalArgumentException("Response is required");
        this.deviceId = deviceId;
        this.status = status;
        this.response = response;
        this.evaluatedAt = evaluatedAt != null ? evaluatedAt : Instant.now();
    }

    public void update(DeviceStatus status, ResponseAction response, Instant evaluatedAt) {
        if (status == null)
            throw new IllegalArgumentException("Device status is required");
        if (response == null)
            throw new IllegalArgumentException("Response is required");
        this.status = status;
        this.response = response;
        this.evaluatedAt = evaluatedAt != null ? evaluatedAt : Instant.now();
    }
}

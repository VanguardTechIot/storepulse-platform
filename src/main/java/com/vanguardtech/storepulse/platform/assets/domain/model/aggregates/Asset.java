package com.vanguardtech.storepulse.platform.assets.domain.model.aggregates;


import com.vanguardtech.storepulse.platform.assets.domain.model.entities.IoTDevice;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.AssetStatus;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "assets")
@Getter
public class Asset extends AuditableAbstractAggregateRoot<Asset> {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    @Column(nullable = false)
    private Long resourceId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "iot_device_id", nullable = false, unique = true)
    private IoTDevice iotDevice;

    protected Asset() {
    }

    public Asset(Long resourceId, String name, IoTDevice iotDevice) {
        if (resourceId == null || resourceId <= 0)
            throw new IllegalArgumentException("Resource id must be a positive number");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Asset name is required");
        if (iotDevice == null)
            throw new IllegalArgumentException("IoT device is required");
        this.resourceId = resourceId;
        this.name = name;
        this.iotDevice = iotDevice;
        this.status = AssetStatus.ACTIVE;
    }

    public void deactivateDevice(String reason){
        iotDevice.deactivate(reason);
        this.status = AssetStatus.INACTIVE;
    }

    public void reactivateDevice(){
        iotDevice.reactivate();
        this.status = AssetStatus.ACTIVE;
    }
}

package com.vanguardtech.storepulse.platform.assets.domain.model.entities;


import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.AssetStatus;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.ConnectivityStatus;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.IoTDeviceType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * IoT node installed in a stand or common area: one controller with its sensors, meters,
 * and actuators. It is only created or changed through its assets.
 */
@Entity
@Table(name = "iot_devices")
@Getter
public class IoTDevice {

    public static final double MIN_SUPPLY_VOLTAGE = 4.5;
    public static final double MAX_SUPPLY_VOLTAGE = 5.5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    private String manufacturer;

    private String firmwareVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IoTDeviceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectivityStatus connectivity;

    private Instant lastReportAt;

    private Double supplyVoltage;

    /** Events kept on the device while offline, waiting to be synchronized. */
    private int bufferedEvents;

    private String deactivationReason;

    @ElementCollection
    @CollectionTable(name = "iot_device_actuators", joinColumns = @JoinColumn(name = "iot_device_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private List<AccessType> actuators = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "iot_device_id")
    private List<Sensor> sensors = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "iot_device_id")
    private List<Meter> meters = new ArrayList<>();

    protected IoTDevice() {
    }

    public IoTDevice(String serialNumber, IoTDeviceType type, String manufacturer, String firmwareVersion) {
        if (serialNumber == null || serialNumber.trim().isEmpty())
            throw new IllegalArgumentException("Serial number is required");
        if (type == null)
            throw new IllegalArgumentException("Device type is required");
        this.serialNumber = serialNumber.trim().toUpperCase();
        this.type = type;
        this.manufacturer = manufacturer;
        this.firmwareVersion = firmwareVersion;
        this.status = AssetStatus.ACTIVE;
        this.connectivity = ConnectivityStatus.DISCONNECTED;
        this.bufferedEvents = 0;
    }

    public boolean isActive() {
        return status == AssetStatus.ACTIVE;
    }

    public boolean isConnected() {
        return isActive() && connectivity == ConnectivityStatus.CONNECTED;
    }

    /** A device is faulty when its last supply voltage is outside the accepted range. */
    public boolean hasFault(){
        return supplyVoltage != null
                && (supplyVoltage < MIN_SUPPLY_VOLTAGE || supplyVoltage > MAX_SUPPLY_VOLTAGE);
    }

    /** Stops data reception: the stand if left without monitoring until it is reactivated. */
    public void deactivate(String reason){
        if (!isActive())
            throw new IllegalStateException("Device " + serialNumber + " is already inactive");
        this.status = AssetStatus.INACTIVE;
        this.deactivationReason = reason;
    }

    public void reactivate(){
        if (isActive())
            throw new IllegalStateException("Device " + serialNumber + " is already active");
        this.status = AssetStatus.ACTIVE;
        this.deactivationReason = null;
    }

    public void addSensor(Sensor sensor){
        sensors.add(sensor);
    }

    public void addMeter(Meter meter){
        meters.add(meter);
    }

    public void addActuator(AccessType actuator){
        if(!actuators.contains(actuator))
            actuators.add(actuator);
    }
}
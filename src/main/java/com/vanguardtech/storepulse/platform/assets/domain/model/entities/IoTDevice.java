package com.vanguardtech.storepulse.platform.assets.domain.model.entities;


import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.ActuatorType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.AssetStatus;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.ConnectivityStatus;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.IoTDeviceType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.MeterType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.assets.domain.exceptions.AssetsError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.Duration;
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

    /** Time without reports after which the device is considered disconnected. */
    public static final Duration DISCONNECTION_THRESHOLD = Duration.ofMinutes(15);

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
    private List<ActuatorType> actuators = new ArrayList<>();

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
        equipFor(type);
    }

    public boolean isActive() {
        return status == AssetStatus.ACTIVE;
    }

    /** Connected while the last report is recent; disconnected if it never reported or stopped reporting. */
    public ConnectivityStatus getConnectivity() {
        if (lastReportAt == null || lastReportAt.isBefore(Instant.now().minus(DISCONNECTION_THRESHOLD)))
            return ConnectivityStatus.DISCONNECTED;
        return ConnectivityStatus.CONNECTED;
    }

    public boolean isDisconnected() {
        return isActive() && getConnectivity() == ConnectivityStatus.DISCONNECTED;
    }

    /** A device is faulty when its last supply voltage is outside the accepted range. */
    public boolean hasFault(){
        return supplyVoltage != null
                && (supplyVoltage < MIN_SUPPLY_VOLTAGE || supplyVoltage > MAX_SUPPLY_VOLTAGE);
    }

    /** Stops data reception; the stand is left without monitoring until it is reactivated. */
    public void deactivate(String reason) {
        if (!isActive())
            throw new BusinessRuleException(AssetsError.IOT_DEVICE_ALREADY_INACTIVE);
        this.status = AssetStatus.INACTIVE;
        this.deactivationReason = reason;
    }

    public void reactivate() {
        if (isActive())
            throw new BusinessRuleException(AssetsError.IOT_DEVICE_ALREADY_ACTIVE);
        this.status = AssetStatus.ACTIVE;
        this.deactivationReason = null;
    }

    /**
     * Records that the device reported. Voltage and buffered events are only updated when the
     * report includes them. Reports from an inactive device are ignored.
     */
    public void recordReport(Instant reportedAt, Double supplyVoltage, Integer bufferedEvents) {
        if (!isActive()) return;
        this.lastReportAt = reportedAt;
        this.connectivity = ConnectivityStatus.CONNECTED;
        if (supplyVoltage != null) this.supplyVoltage = supplyVoltage;
        if (bufferedEvents != null) this.bufferedEvents = bufferedEvents;
    }

    public void addSensor(Sensor sensor){
        sensors.add(sensor);
    }

    public void addMeter(Meter meter){
        meters.add(meter);
    }

    public void addActuator(ActuatorType actuator){
        if(!actuators.contains(actuator))
            actuators.add(actuator);
    }
    /** Installs the sensors, meters and actuators that each node type has in the IoT device design. */
    private void equipFor(IoTDeviceType type) {
        switch (type) {
            case INTRUSION_NODE -> {
                addSensor(new Sensor(SensorType.MOTION, null));
                addActuator(ActuatorType.ALARM);
            }
            case SMOKE_NODE -> {
                addSensor(new Sensor(SensorType.SMOKE, "ppm"));
                addSensor(new Sensor(SensorType.TEMPERATURE, "°C"));
                addSensor(new Sensor(SensorType.HUMIDITY, "%"));
            }
            case CONSUMPTION_NODE -> {
                addMeter(new Meter(MeterType.ELECTRICITY));
                addMeter(new Meter(MeterType.WATER));
            }
            case COMMON_AREA_NODE -> addSensor(new Sensor(SensorType.MOTION, null));
            case CAMERA_NODE -> {
                // The camera produces images, handled by the edge; it has no sensor readings.
            }
        }
        addActuator(ActuatorType.LED);
    }
}
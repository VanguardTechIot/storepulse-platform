package com.vanguardtech.storepulse.platform.assets.domain.model.entities;

import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.ActuatorType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.IoTDeviceType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.MeterType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.SensorType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IoTDeviceTest {

    @Test
    void anIntrusionNodeHasAMotionSensorAnAlarmAndALed() {
        var device = new IoTDevice("SP-001", IoTDeviceType.INTRUSION_NODE, null, null);

        assertEquals(List.of(SensorType.MOTION), device.getSensors().stream().map(Sensor::getType).toList());
        assertEquals(List.of(ActuatorType.ALARM, ActuatorType.LED), device.getActuators());
        assertTrue(device.getMeters().isEmpty());
    }

    @Test
    void aSmokeNodeHasSmokeTemperatureAndHumiditySensorsButNoAlarm() {
        var device = new IoTDevice("SP-002", IoTDeviceType.SMOKE_NODE, null, null);

        assertEquals(List.of(SensorType.SMOKE, SensorType.TEMPERATURE, SensorType.HUMIDITY),
                device.getSensors().stream().map(Sensor::getType).toList());
        assertEquals(List.of(ActuatorType.LED), device.getActuators());
    }

    @Test
    void aConsumptionNodeHasElectricityAndWaterMeters() {
        var device = new IoTDevice("SP-003", IoTDeviceType.CONSUMPTION_NODE, null, null);

        assertEquals(List.of(MeterType.ELECTRICITY, MeterType.WATER),
                device.getMeters().stream().map(Meter::getType).toList());
        assertTrue(device.getSensors().isEmpty());
        assertEquals(List.of(ActuatorType.LED), device.getActuators());
    }

    @Test
    void aCameraNodeOnlyHasAStatusLed() {
        var device = new IoTDevice("SP-004", IoTDeviceType.CAMERA_NODE, null, null);

        assertTrue(device.getSensors().isEmpty());
        assertTrue(device.getMeters().isEmpty());
        assertEquals(List.of(ActuatorType.LED), device.getActuators());
    }

}

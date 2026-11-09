package com.vanguardtech.storepulse.platform.assets.domain.model.entities;

import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.ActuatorType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.IoTDeviceType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.MeterType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.SensorType;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.ConnectivityStatus;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.Duration;
import java.time.Instant;

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

    @Test
    void aDeviceCannotBeDeactivatedTwice() {
        var device = new IoTDevice("SP-005", IoTDeviceType.INTRUSION_NODE, null, null);
        device.deactivate("Maintenance");

        var error = assertThrows(BusinessRuleException.class, () -> device.deactivate("Again"));

        assertEquals("iot_device_already_inactive", error.getMessage());
        assertEquals("Maintenance", device.getDeactivationReason());
    }

    @Test
    void anActiveDeviceCannotBeReactivated() {
        var device = new IoTDevice("SP-006", IoTDeviceType.INTRUSION_NODE, null, null);

        var error = assertThrows(BusinessRuleException.class, device::reactivate);

        assertEquals("iot_device_already_active", error.getMessage());
    }

    @Test
    void aDeviceThatNeverReportedIsDisconnected() {
        var device = new IoTDevice("SP-007", IoTDeviceType.SMOKE_NODE, null, null);

        assertEquals(ConnectivityStatus.DISCONNECTED, device.getConnectivity());
        assertTrue(device.isDisconnected());
    }

    @Test
    void aRecentReportConnectsTheDevice() {
        var device = new IoTDevice("SP-008", IoTDeviceType.SMOKE_NODE, null, null);

        device.recordReport(Instant.now(), 5.02, 3);

        assertEquals(ConnectivityStatus.CONNECTED, device.getConnectivity());
        assertFalse(device.isDisconnected());
        assertEquals(3, device.getBufferedEvents());
        assertFalse(device.hasFault());
    }

    @Test
    void aDeviceWithoutReportsForFifteenMinutesIsDisconnected() {
        var device = new IoTDevice("SP-009", IoTDeviceType.SMOKE_NODE, null, null);

        device.recordReport(Instant.now().minus(Duration.ofMinutes(16)), 5.0, 0);

        assertEquals(ConnectivityStatus.DISCONNECTED, device.getConnectivity());
        assertTrue(device.isDisconnected());
    }

    @Test
    void aVoltageOutsideTheAcceptedRangeIsAFault() {
        var device = new IoTDevice("SP-010", IoTDeviceType.INTRUSION_NODE, null, null);

        device.recordReport(Instant.now(), 4.31, 0);

        assertTrue(device.hasFault());
    }

    @Test
    void anInactiveDeviceIgnoresItsReports() {
        var device = new IoTDevice("SP-011", IoTDeviceType.INTRUSION_NODE, null, null);
        device.deactivate("Maintenance");

        device.recordReport(Instant.now(), 5.0, 7);

        assertNull(device.getLastReportAt());
        assertEquals(0, device.getBufferedEvents());
        assertFalse(device.isDisconnected());
    }
}

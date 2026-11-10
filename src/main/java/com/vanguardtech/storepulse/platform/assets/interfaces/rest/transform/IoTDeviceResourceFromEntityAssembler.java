package com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.IoTDeviceResource;

public class IoTDeviceResourceFromEntityAssembler {

    public static IoTDeviceResource toResourceFromEntity(Asset asset){
        var device = asset.getIotDevice();
        return new IoTDeviceResource(
                device.getId(),
                asset.getId(),
                asset.getResourceId(),
                device.getSerialNumber(),
                device.getManufacturer(),
                device.getFirmwareVersion(),
                device.getType().name(),
                device.getStatus().name(),
                device.getConnectivity().name(),
                device.getLastReportAt(),
                device.getSupplyVoltage(),
                device.getBufferedEvents(),
                device.getDeactivationReason(),
                device.getActuators().stream().map(Enum::name).toList()
        );
    }
}

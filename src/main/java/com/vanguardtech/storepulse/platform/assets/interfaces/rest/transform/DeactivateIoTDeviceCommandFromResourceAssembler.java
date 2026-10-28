package com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.assets.domain.model.commands.DeactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.DeactivateIoTDeviceResource;

public class DeactivateIoTDeviceCommandFromResourceAssembler {

    public static DeactivateIoTDeviceCommand toCommandFromResource(Long iotDeviceId, DeactivateIoTDeviceResource resource){
        var reason = resource == null ? null : resource.reason();
        return new DeactivateIoTDeviceCommand(iotDeviceId, reason);
    }
}

package com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.assets.domain.model.commands.RegisterIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.IoTDeviceType;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.RegisterIoTDeviceResource;

public class RegisterIoTDeviceCommandFromResourceAssembler {

    public static RegisterIoTDeviceCommand toCommandFromResource(RegisterIoTDeviceResource resource){
        if (resource.type() == null)
            throw new IllegalArgumentException("Device type is required");
        return new RegisterIoTDeviceCommand(
                resource.serialNumber(),
                IoTDeviceType.valueOf(resource.type()),
                resource.resourceId()
        );
    }
}

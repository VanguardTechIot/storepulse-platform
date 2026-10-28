package com.vanguardtech.storepulse.platform.assets.domain.services;


import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.DeactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.ReactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.RegisterIoTDeviceCommand;

import java.util.Optional;

public interface AssetCommandService {

    Optional<Asset> handle(RegisterIoTDeviceCommand command);
    Optional<Asset> handle(DeactivateIoTDeviceCommand command);
    Optional<Asset> handle(ReactivateIoTDeviceCommand command);
}

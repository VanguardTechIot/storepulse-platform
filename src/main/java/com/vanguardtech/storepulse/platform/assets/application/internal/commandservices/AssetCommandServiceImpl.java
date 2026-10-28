package com.vanguardtech.storepulse.platform.assets.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.DeactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.ReactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.RegisterIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.entities.IoTDevice;
import com.vanguardtech.storepulse.platform.assets.domain.services.AssetCommandService;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.AssetRepository;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssetCommandServiceImpl implements AssetCommandService {

    private final AssetRepository assetRepository;
    private final ResourceRepository resourceRepository;

    public AssetCommandServiceImpl(AssetRepository assetRepository, ResourceRepository resourceRepository) {
        this.assetRepository = assetRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public Optional<Asset> handle(RegisterIoTDeviceCommand command){
        var serialNumber = command.serialNumber().trim().toUpperCase();
        if (assetRepository.existsByIotDevice_SerialNumber(serialNumber))
            throw new IllegalArgumentException("The identifier" + serialNumber + " is already registered");

        var resource = resourceRepository.findById(command.resourceId())
                .orElseThrow(() -> new IllegalArgumentException("Resource with id " + command.resourceId() + " not found"));

        var device = new IoTDevice(serialNumber, command.type(), null, null);
        var asset = new Asset(resource.getId(), "Iot node" + resource.getName(), device);
        return Optional.of(assetRepository.save(asset));
    }

    @Override
    public Optional<Asset> handle(DeactivateIoTDeviceCommand command) {
        var asset = findByDeviceId(command.iotDeviceId());
        asset.deactivateDevice(command.reason());
        return Optional.of(assetRepository.save(asset));
    }

    @Override
    public Optional<Asset> handle(ReactivateIoTDeviceCommand command) {
        var asset = findByDeviceId(command.iotDeviceId());
        asset.reactivateDevice();
        return Optional.of(assetRepository.save(asset));
    }

    private Asset findByDeviceId(Long iotDeviceId){
        return assetRepository.findByIotDevice_Id(iotDeviceId)
                .orElseThrow(() -> new IllegalArgumentException("IoT device" + iotDeviceId + " not found"));
    }
}

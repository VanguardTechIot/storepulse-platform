package com.vanguardtech.storepulse.platform.assets.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.DeactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.ReactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.RecordIoTDeviceReportCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.RegisterIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.entities.IoTDevice;
import com.vanguardtech.storepulse.platform.assets.domain.services.AssetCommandService;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.AssetRepository;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.ResourceRepository;
import com.vanguardtech.storepulse.platform.assets.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.assets.domain.exceptions.AssetsError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssetCommandServiceImpl implements AssetCommandService {

    private final AssetRepository assetRepository;
    private final ResourceRepository resourceRepository;
    private final ExternalPropertyManagementService externalPropertyManagementService;

    public AssetCommandServiceImpl(AssetRepository assetRepository, ResourceRepository resourceRepository,
                                   ExternalPropertyManagementService externalPropertyManagementService) {
        this.assetRepository = assetRepository;
        this.resourceRepository = resourceRepository;
        this.externalPropertyManagementService = externalPropertyManagementService;
    }

    @Override
    public Optional<Asset> handle(RegisterIoTDeviceCommand command){
        var serialNumber = command.serialNumber().trim().toUpperCase();
        if (assetRepository.existsByIotDevice_SerialNumber(serialNumber))
            throw new BusinessRuleException(AssetsError.SERIAL_NUMBER_ALREADY_REGISTERED);

        var resource = resourceRepository.findById(command.resourceId())
                .filter(found -> externalPropertyManagementService.isResourceOfAdministrator(found.getId(),
                        command.administratorId()))
                .orElseThrow(() -> new ResourceNotFoundException(AssetsError.RESOURCE_NOT_FOUND));

        var device = new IoTDevice(serialNumber, command.type(), null, null);
        var asset = new Asset(resource.getId(), "IoT node " + resource.getName(), device);
        return Optional.of(assetRepository.save(asset));
    }

    @Override
    public Optional<Asset> handle(DeactivateIoTDeviceCommand command) {
        var asset = findDeviceOfAdministrator(command.iotDeviceId(), command.administratorId());
        asset.deactivateDevice(command.reason());
        return Optional.of(assetRepository.save(asset));
    }

    @Override
    public Optional<Asset> handle(ReactivateIoTDeviceCommand command) {
        var asset = findDeviceOfAdministrator(command.iotDeviceId(), command.administratorId());
        asset.reactivateDevice();
        return Optional.of(assetRepository.save(asset));
    }

    @Override
    public Optional<Asset> handle(RecordIoTDeviceReportCommand command) {
        var asset = findByDeviceId(command.iotDeviceId());
        asset.recordDeviceReport(command.reportedAt(), command.supplyVoltage(), command.bufferedEvents());
        return Optional.of(assetRepository.save(asset));
    }

    /** A device installed outside the administrator's gallery is answered as not found. */
    private Asset findDeviceOfAdministrator(Long iotDeviceId, Long administratorId) {
        return assetRepository.findByIotDevice_Id(iotDeviceId)
                .filter(asset -> externalPropertyManagementService.isResourceOfAdministrator(asset.getResourceId(),
                        administratorId))
                .orElseThrow(() -> new ResourceNotFoundException(AssetsError.IOT_DEVICE_NOT_FOUND));
    }

    private Asset findByDeviceId(Long iotDeviceId) {
        return assetRepository.findByIotDevice_Id(iotDeviceId)
                .orElseThrow(() -> new ResourceNotFoundException(AssetsError.IOT_DEVICE_NOT_FOUND));
    }
}
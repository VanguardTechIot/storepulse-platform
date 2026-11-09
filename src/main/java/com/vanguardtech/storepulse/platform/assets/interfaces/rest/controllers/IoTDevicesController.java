package com.vanguardtech.storepulse.platform.assets.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.assets.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.assets.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.assets.domain.exceptions.AssetsError;
import com.vanguardtech.storepulse.platform.assets.domain.model.commands.ReactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetIoTDeviceByIdQuery;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetIoTDevicesByResourceIdsQuery;
import com.vanguardtech.storepulse.platform.assets.domain.services.AssetCommandService;
import com.vanguardtech.storepulse.platform.assets.domain.services.AssetQueryService;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.DeactivateIoTDeviceResource;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.IoTDeviceResource;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.RegisterIoTDeviceResource;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform.DeactivateIoTDeviceCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform.IoTDeviceResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform.RegisterIoTDeviceCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iot-devices")
@Tag(name = "IoT Devices", description = "Registration, status and activation of IoT devices")
public class IoTDevicesController {

    private final AssetCommandService assetCommandService;
    private final AssetQueryService assetQueryService;

    private final ExternalIamService externalIamService;
    private final ExternalPropertyManagementService externalPropertyManagementService;

    public IoTDevicesController(AssetCommandService assetCommandService, AssetQueryService assetQueryService,
                                ExternalIamService externalIamService,
                                ExternalPropertyManagementService externalPropertyManagementService) {
        this.assetCommandService = assetCommandService;
        this.assetQueryService = assetQueryService;
        this.externalIamService = externalIamService;
        this.externalPropertyManagementService = externalPropertyManagementService;
    }

    @Operation(summary = "Get the devices installed in the administrator's gallery")
    @GetMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public ResponseEntity<List<IoTDeviceResource>> getAllIoTDevices(){
        var resourceIds = externalPropertyManagementService.fetchResourceIdsOfAdministrator(
                externalIamService.fetchAuthenticatedAdministratorId());
        var devices = assetQueryService.handle(new GetIoTDevicesByResourceIdsQuery(resourceIds)).stream()
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(devices);
    }

    @Operation(summary = "Get a device installed in the administrator's gallery",
            description = "Answers 404 iot_device_not_found.")
    @GetMapping("/{iotDeviceId}")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public ResponseEntity<IoTDeviceResource> getIoTDeviceById(@PathVariable Long iotDeviceId) {
        var resourceIds = externalPropertyManagementService.fetchResourceIdsOfAdministrator(
                externalIamService.fetchAuthenticatedAdministratorId());
        return assetQueryService.handle(new GetIoTDeviceByIdQuery(iotDeviceId))
                .filter(asset -> resourceIds.contains(asset.getResourceId()))
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(AssetsError.IOT_DEVICE_NOT_FOUND));
    }

    @Operation(summary = "Install a device on a resource linked to a unit of the administrator's gallery",
            description = "Answers 404 resource_not_found and 409 serial_number_already_registered.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public ResponseEntity<IoTDeviceResource> registerIoTDevice(@Valid @RequestBody RegisterIoTDeviceResource resource) {
        var command = RegisterIoTDeviceCommandFromResourceAssembler.toCommandFromResource(resource,
                externalIamService.fetchAuthenticatedAdministratorId());
        return assetCommandService.handle(command)
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .map(device -> ResponseEntity.status(HttpStatus.CREATED).body(device))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Deactivate a device of the administrator's gallery",
            description = "Answers 404 iot_device_not_found and 409 iot_device_already_inactive.")
    @PatchMapping("/{iotDeviceId}/deactivation")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public ResponseEntity<IoTDeviceResource> deactivateIoTDevice(
            @PathVariable Long iotDeviceId,
            @RequestBody(required = false) DeactivateIoTDeviceResource resource) {
        var command = DeactivateIoTDeviceCommandFromResourceAssembler.toCommandFromResource(iotDeviceId,
                externalIamService.fetchAuthenticatedAdministratorId(), resource);
        return assetCommandService.handle(command)
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Reactivate a device of the administrator's gallery",
            description = "Answers 404 iot_device_not_found and 409 iot_device_already_active.")
    @PatchMapping("/{iotDeviceId}/reactivation")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public ResponseEntity<IoTDeviceResource> reactivateIoTDevice(@PathVariable Long iotDeviceId) {
        var command = new ReactivateIoTDeviceCommand(iotDeviceId, externalIamService.fetchAuthenticatedAdministratorId());
        return assetCommandService.handle(command)
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
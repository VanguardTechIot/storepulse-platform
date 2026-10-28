package com.vanguardtech.storepulse.platform.assets.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.assets.domain.model.commands.ReactivateIoTDeviceCommand;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetAllIoTDevicesQuery;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetIoTDeviceByIdQuery;
import com.vanguardtech.storepulse.platform.assets.domain.services.AssetCommandService;
import com.vanguardtech.storepulse.platform.assets.domain.services.AssetQueryService;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.DeactivateIoTDeviceResource;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.IoTDeviceResource;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.RegisterIoTDeviceResource;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform.DeactivateIoTDeviceCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform.IoTDeviceResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform.RegisterIoTDeviceCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iot-devices")
@Tag(name = "IoT Devices", description = "Registration, status and activation of IoT devices")
public class IoTDevicesController {

    private final AssetCommandService assetCommandService;
    private final AssetQueryService assetQueryService;

    public IoTDevicesController(AssetCommandService assetCommandService, AssetQueryService assetQueryService) {
        this.assetCommandService = assetCommandService;
        this.assetQueryService = assetQueryService;
    }

    @GetMapping
    public ResponseEntity<List<IoTDeviceResource>> getAllIoTDevices(){
        var devices = assetQueryService.handle(new GetAllIoTDevicesQuery()).stream()
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{iotDeviceId}")
    public ResponseEntity<IoTDeviceResource> getIoTDeviceById(@PathVariable Long iotDeviceId) {
        return assetQueryService.handle(new GetIoTDeviceByIdQuery(iotDeviceId))
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<IoTDeviceResource> registerIoTDevice(@RequestBody RegisterIoTDeviceResource resource) {
        var command = RegisterIoTDeviceCommandFromResourceAssembler.toCommandFromResource(resource);
        return assetCommandService.handle(command)
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .map(device -> ResponseEntity.status(HttpStatus.CREATED).body(device))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{iotDeviceId}/deactivation")
    public ResponseEntity<IoTDeviceResource> deactivateIoTDevice(
            @PathVariable Long iotDeviceId,
            @RequestBody(required = false) DeactivateIoTDeviceResource resource) {
        var command = DeactivateIoTDeviceCommandFromResourceAssembler.toCommandFromResource(iotDeviceId, resource);
        return assetCommandService.handle(command)
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{iotDeviceId}/reactivation")
    public ResponseEntity<IoTDeviceResource> reactivateIoTDevice(@PathVariable Long iotDeviceId) {
        return assetCommandService.handle(new ReactivateIoTDeviceCommand(iotDeviceId))
                .map(IoTDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}

package com.vanguardtech.storepulse.platform.safety.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetDeviceStateByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.safety.domain.services.DeviceStateQueryService;
import com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources.DeviceStateResource;
import com.vanguardtech.storepulse.platform.safety.interfaces.rest.transform.DeviceStateResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/device-states", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Device States", description = "Current state and response of each device according to the safety rules.")
public class DeviceStatesController {

    private final DeviceStateQueryService deviceStateQueryService;

    @Operation(summary = "Get the current state of a device")
    @GetMapping
    public ResponseEntity<DeviceStateResource> getDeviceState(@RequestParam Long deviceId) {
        return deviceStateQueryService.handle(new GetDeviceStateByDeviceIdQuery(deviceId))
                .map(DeviceStateResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

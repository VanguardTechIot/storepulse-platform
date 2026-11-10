package com.vanguardtech.storepulse.platform.safety.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.safety.application.internal.outboundservices.acl.ExternalAssetsService;
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
    private final ExternalAssetsService externalAssetsService;

    @Operation(summary = "Get the current state of a device visible to the user",
            description = "Answers 404 when the device is not installed in a resource visible to the user.")
    @GetMapping
    public ResponseEntity<DeviceStateResource> getDeviceState(@RequestParam Long deviceId) {
        if (!externalAssetsService.fetchDeviceIdsVisibleToAuthenticatedUser().contains(deviceId))
            return ResponseEntity.notFound().build();
        return deviceStateQueryService.handle(new GetDeviceStateByDeviceIdQuery(deviceId))
                .map(DeviceStateResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
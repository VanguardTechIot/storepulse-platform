package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalResourceAssetService;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.services.SensorStatusQueryService;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.SensorStatusResource;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.transform.SensorStatusResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/sensors", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Sensors", description = "Current status of the sensors of each device.")
public class SensorStatusController {

    private final SensorStatusQueryService sensorStatusQueryService;
    private final ExternalResourceAssetService externalResourceAssetService;

    public SensorStatusController(SensorStatusQueryService sensorStatusQueryService,
                                  ExternalResourceAssetService externalResourceAssetService) {
        this.sensorStatusQueryService = sensorStatusQueryService;
        this.externalResourceAssetService = externalResourceAssetService;
    }

    @Operation(summary = "Get the latest reading of each sensor of a device visible to the user",
            description = "Answers 404 when the device is not installed in a resource visible to the user.")
    @GetMapping("/status")
    public ResponseEntity<List<SensorStatusResource>> getSensorStatus(@RequestParam Long deviceId) {
        if (!externalResourceAssetService.fetchDeviceIdsVisibleToAuthenticatedUser().contains(deviceId)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(sensorStatusQueryService.handle(new GetSensorStatusByDeviceIdQuery(deviceId)).stream()
                .map(SensorStatusResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }
}
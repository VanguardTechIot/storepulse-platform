package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalResourceAssetService;
import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalPropertyCommunicationService;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.queries.GetSensorStatusByDeviceIdsQuery;
import com.vanguardtech.storepulse.platform.monitoring.domain.model.valueobjects.SensorStatus;
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
    private final ExternalPropertyCommunicationService externalPropertyCommunicationService;

    public SensorStatusController(SensorStatusQueryService sensorStatusQueryService,
                                  ExternalResourceAssetService externalResourceAssetService,
                                  ExternalPropertyCommunicationService externalPropertyCommunicationService) {
        this.sensorStatusQueryService = sensorStatusQueryService;
        this.externalResourceAssetService = externalResourceAssetService;
        this.externalPropertyCommunicationService = externalPropertyCommunicationService;
    }

    @Operation(summary = "Get the latest reading of each sensor of a device or of a resource visible to the user",
            description = "Answers 404 when the device or the resource is not visible to the user.")
    @GetMapping("/status")
    public ResponseEntity<List<SensorStatusResource>> getSensorStatus(@RequestParam(required = false) Long deviceId,
                                                                      @RequestParam(required = false) Long resourceId) {
        if ((deviceId == null) == (resourceId == null))
            throw new IllegalArgumentException("Send either deviceId or resourceId");
        if (resourceId != null) {
            if (!externalPropertyCommunicationService.fetchResourceIdsVisibleToAuthenticatedUser().contains(resourceId))
                return ResponseEntity.notFound().build();
            var deviceIds = externalResourceAssetService.fetchDeviceIdsByResourceId(resourceId);
            return ResponseEntity.ok(toResources(sensorStatusQueryService.handle(new GetSensorStatusByDeviceIdsQuery(deviceIds))));
        }
        if (!externalResourceAssetService.fetchDeviceIdsVisibleToAuthenticatedUser().contains(deviceId)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toResources(sensorStatusQueryService.handle(new GetSensorStatusByDeviceIdQuery(deviceId))));
    }

    private List<SensorStatusResource> toResources(List<SensorStatus> statuses) {
        return statuses.stream()
                .map(SensorStatusResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }
}
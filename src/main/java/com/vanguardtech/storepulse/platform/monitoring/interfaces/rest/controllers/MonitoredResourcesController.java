package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalPropertyCommunicationService;
import com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl.ExternalResourceAssetService;
import com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources.MonitoredResourceResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/monitored-resources", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Monitored Resources", description = "Resources visible to the user and the devices installed in them.")
public class MonitoredResourcesController {

    private final ExternalPropertyCommunicationService externalPropertyCommunicationService;
    private final ExternalResourceAssetService externalResourceAssetService;

    public MonitoredResourcesController(ExternalPropertyCommunicationService externalPropertyCommunicationService,
                                        ExternalResourceAssetService externalResourceAssetService) {
        this.externalPropertyCommunicationService = externalPropertyCommunicationService;
        this.externalResourceAssetService = externalResourceAssetService;
    }

    @Operation(summary = "Get the resources visible to the user with their devices")
    @GetMapping
    public ResponseEntity<List<MonitoredResourceResource>> getMonitoredResources() {
        return ResponseEntity.ok(externalPropertyCommunicationService.fetchResourceIdsVisibleToAuthenticatedUser().stream()
                .map(resourceId -> new MonitoredResourceResource(
                        resourceId, externalResourceAssetService.fetchDeviceIdsByResourceId(resourceId)))
                .toList());
    }
}

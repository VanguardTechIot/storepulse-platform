package com.vanguardtech.storepulse.platform.assets.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetAllResourcesQuery;
import com.vanguardtech.storepulse.platform.assets.domain.services.ResourceQueryService;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources.ResourceResource;
import com.vanguardtech.storepulse.platform.assets.interfaces.rest.transform.ResourceResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/resources")
@Tag(name = "Resources", description = "Stands and common areas where IoT devices are installed")
public class ResourcesController {
    private final ResourceQueryService resourceQueryService;

    public ResourcesController(ResourceQueryService resourceQueryService) {
        this.resourceQueryService = resourceQueryService;
    }

    @GetMapping
    public ResponseEntity<List<ResourceResource>> getAllResources() {
        var resources = resourceQueryService.handle(new GetAllResourcesQuery()).stream()
                .map(ResourceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }
}

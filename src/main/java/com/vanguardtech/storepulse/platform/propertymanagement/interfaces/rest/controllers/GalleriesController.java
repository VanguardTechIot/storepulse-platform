package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialUnitsByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.CommercialGalleryResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.CommercialUnitResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.RegisterCommercialGalleryResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.RegisterCommercialUnitResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.UpdateCommercialGalleryResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.CommercialGalleryResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.CommercialUnitResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.RegisterCommercialGalleryCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.RegisterCommercialUnitCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.UpdateCommercialGalleryInfoCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/galleries")
@Tag(name = "Galleries", description = "Commercial gallery of the administrator and its units")
public class GalleriesController {

    private final CommercialGalleryCommandService galleryCommandService;
    private final CommercialGalleryQueryService galleryQueryService;
    private final CommercialUnitCommandService unitCommandService;
    private final CommercialUnitQueryService unitQueryService;
    private final ExternalIamService externalIamService;

    public GalleriesController(CommercialGalleryCommandService galleryCommandService,
                               CommercialGalleryQueryService galleryQueryService,
                               CommercialUnitCommandService unitCommandService,
                               CommercialUnitQueryService unitQueryService,
                               ExternalIamService externalIamService) {
        this.galleryCommandService = galleryCommandService;
        this.galleryQueryService = galleryQueryService;
        this.unitCommandService = unitCommandService;
        this.unitQueryService = unitQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get the gallery of the authenticated administrator (an empty list when it has none)")
    @GetMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public List<CommercialGalleryResource> getGalleriesOfAuthenticatedAdministrator() {
        var administratorId = externalIamService.fetchAuthenticatedAdministratorId();
        return galleryQueryService.handle(new GetCommercialGalleryByAdministratorIdQuery(administratorId))
                .map(CommercialGalleryResourceFromEntityAssembler::toResourceFromEntity)
                .map(List::of)
                .orElseGet(List::of);
    }

    @Operation(summary = "Get the administrator's gallery by id",
            description = "Answers 404 gallery_not_found when the gallery belongs to another administrator.")
    @GetMapping("/{galleryId}")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public CommercialGalleryResource getGalleryById(@PathVariable Long galleryId) {
        return CommercialGalleryResourceFromEntityAssembler.toResourceFromEntity(
                findGalleryOfAuthenticatedAdministrator(galleryId));
    }

    @Operation(summary = "Register the gallery of the authenticated administrator",
            description = "Answers 409 gallery_already_registered when the administrator already has one.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CommercialGalleryResource registerGallery(@Valid @RequestBody RegisterCommercialGalleryResource resource) {
        var administratorId = externalIamService.fetchAuthenticatedAdministratorId();
        var command = RegisterCommercialGalleryCommandFromResourceAssembler.toCommandFromResource(administratorId, resource);
        return CommercialGalleryResourceFromEntityAssembler.toResourceFromEntity(galleryCommandService.handle(command));
    }

    @Operation(summary = "Update the general data of the administrator's gallery",
            description = "Answers 404 gallery_not_found when the gallery belongs to another administrator.")
    @PutMapping("/{galleryId}")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public CommercialGalleryResource updateGallery(@PathVariable Long galleryId,
                                                   @Valid @RequestBody UpdateCommercialGalleryResource resource) {
        var command = UpdateCommercialGalleryInfoCommandFromResourceAssembler.toCommandFromResource(galleryId,
                externalIamService.fetchAuthenticatedAdministratorId(), resource);
        return CommercialGalleryResourceFromEntityAssembler.toResourceFromEntity(galleryCommandService.handle(command));
    }

    @Operation(summary = "Get the units of the administrator's gallery, removed ones included",
            description = "Answers 404 gallery_not_found when the gallery belongs to another administrator.")
    @GetMapping("/{galleryId}/units")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public List<CommercialUnitResource> getUnitsByGalleryId(@PathVariable Long galleryId) {
        findGalleryOfAuthenticatedAdministrator(galleryId);
        return unitQueryService.handle(new GetCommercialUnitsByGalleryIdQuery(galleryId)).stream()
                .map(CommercialUnitResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }

    @Operation(summary = "Register a store or a common area in the gallery",
            description = "Answers 404 gallery_not_found and 409 duplicated_unit_code or duplicated_common_area_name.")
    @PostMapping("/{galleryId}/units")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CommercialUnitResource registerUnit(@PathVariable Long galleryId,
                                               @Valid @RequestBody RegisterCommercialUnitResource resource) {
        var command = RegisterCommercialUnitCommandFromResourceAssembler.toCommandFromResource(galleryId,
                externalIamService.fetchAuthenticatedAdministratorId(), resource);
        return CommercialUnitResourceFromEntityAssembler.toResourceFromEntity(unitCommandService.handle(command));
    }

    private CommercialGallery findGalleryOfAuthenticatedAdministrator(Long galleryId) {
        var administratorId = externalIamService.fetchAuthenticatedAdministratorId();
        return galleryQueryService.handle(new GetCommercialGalleryByAdministratorIdQuery(administratorId))
                .filter(gallery -> gallery.getId().equals(galleryId))
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.GALLERY_NOT_FOUND));
    }
}
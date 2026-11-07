package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByIdQuery;
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
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
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

    @Operation(summary = "Get the galleries of an administrator (at most one)")
    @GetMapping
    public List<CommercialGalleryResource> getGalleriesByAdministratorId(@RequestParam Long administratorId) {
        return galleryQueryService.handle(new GetCommercialGalleryByAdministratorIdQuery(administratorId))
                .map(CommercialGalleryResourceFromEntityAssembler::toResourceFromEntity)
                .map(List::of)
                .orElseGet(List::of);
    }

    @Operation(summary = "Get a gallery by id")
    @GetMapping("/{galleryId}")
    public CommercialGalleryResource getGalleryById(@PathVariable Long galleryId) {
        return galleryQueryService.handle(new GetCommercialGalleryByIdQuery(galleryId))
                .map(CommercialGalleryResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.GALLERY_NOT_FOUND));
    }

    @Operation(summary = "Register the gallery of the authenticated administrator",
            description = "Answers 409 gallery_already_registered when the administrator already has one.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CommercialGalleryResource registerGallery(@Valid @RequestBody RegisterCommercialGalleryResource resource) {
        var administratorId = externalIamService.fetchAuthenticatedAdministratorId()
                .orElseThrow(() -> new BusinessRuleException(PropertyManagementError.ADMINISTRATOR_NOT_AUTHENTICATED));
        var command = RegisterCommercialGalleryCommandFromResourceAssembler.toCommandFromResource(administratorId, resource);
        return CommercialGalleryResourceFromEntityAssembler.toResourceFromEntity(galleryCommandService.handle(command));
    }

    @Operation(summary = "Update the general data of a gallery")
    @PutMapping("/{galleryId}")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public CommercialGalleryResource updateGallery(@PathVariable Long galleryId,
                                                   @Valid @RequestBody UpdateCommercialGalleryResource resource) {
        var command = UpdateCommercialGalleryInfoCommandFromResourceAssembler.toCommandFromResource(galleryId, resource);
        return CommercialGalleryResourceFromEntityAssembler.toResourceFromEntity(galleryCommandService.handle(command));
    }

    @Operation(summary = "Get the units of a gallery, removed ones included")
    @GetMapping("/{galleryId}/units")
    public List<CommercialUnitResource> getUnitsByGalleryId(@PathVariable Long galleryId) {
        return unitQueryService.handle(new GetCommercialUnitsByGalleryIdQuery(galleryId)).stream()
                .map(CommercialUnitResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }

    @Operation(summary = "Register a store or a common area in the gallery",
            description = "Answers 409 duplicated_unit_code or duplicated_common_area_name.")
    @PostMapping("/{galleryId}/units")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CommercialUnitResource registerUnit(@PathVariable Long galleryId,
                                               @Valid @RequestBody RegisterCommercialUnitResource resource) {
        var command = RegisterCommercialUnitCommandFromResourceAssembler.toCommandFromResource(galleryId, resource);
        return CommercialUnitResourceFromEntityAssembler.toResourceFromEntity(unitCommandService.handle(command));
    }
}

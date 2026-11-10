package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.InviteTenantCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RemoveCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialUnitByIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.TenantInvitationCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.CommercialUnitResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.InviteTenantResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.TenantInvitationResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.UpdateCommercialUnitResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.CommercialUnitResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.TenantInvitationResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.UpdateCommercialUnitCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/units")
@Tag(name = "Commercial Units", description = "Stores and common areas of a gallery")
public class UnitsController {

    private final CommercialUnitCommandService unitCommandService;
    private final CommercialUnitQueryService unitQueryService;
    private final CommercialGalleryQueryService galleryQueryService;
    private final TenantInvitationCommandService invitationCommandService;
    private final ExternalIamService externalIamService;

    public UnitsController(CommercialUnitCommandService unitCommandService, CommercialUnitQueryService unitQueryService,
                           CommercialGalleryQueryService galleryQueryService,
                           TenantInvitationCommandService invitationCommandService,
                           ExternalIamService externalIamService) {
        this.unitCommandService = unitCommandService;
        this.unitQueryService = unitQueryService;
        this.galleryQueryService = galleryQueryService;
        this.invitationCommandService = invitationCommandService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get a unit of the administrator's gallery by id",
            description = "Answers 404 unit_not_found when the unit belongs to another gallery.")
    @GetMapping("/{unitId}")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public CommercialUnitResource getUnitById(@PathVariable Long unitId) {
        var galleryId = galleryQueryService.handle(new GetCommercialGalleryByAdministratorIdQuery(
                externalIamService.fetchAuthenticatedAdministratorId())).map(CommercialGallery::getId);
        return unitQueryService.handle(new GetCommercialUnitByIdQuery(unitId))
                .filter(unit -> galleryId.map(unit.getGalleryId()::equals).orElse(false))
                .map(CommercialUnitResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.UNIT_NOT_FOUND));
    }

    @Operation(summary = "Edit a unit, change its status or remove it logically with active=false",
            description = "Answers 404 unit_not_found and 409 duplicated_unit_code, unit_occupied or unit_not_available.")
    @PatchMapping("/{unitId}")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public CommercialUnitResource updateUnit(@PathVariable Long unitId,
                                             @RequestBody UpdateCommercialUnitResource resource) {
        var administratorId = externalIamService.fetchAuthenticatedAdministratorId();
        if (Boolean.FALSE.equals(resource.active()))
            return CommercialUnitResourceFromEntityAssembler.toResourceFromEntity(
                    unitCommandService.handle(new RemoveCommercialUnitCommand(unitId, administratorId)));
        var command = UpdateCommercialUnitCommandFromResourceAssembler.toCommandFromResource(unitId, administratorId,
                resource);
        return CommercialUnitResourceFromEntityAssembler.toResourceFromEntity(unitCommandService.handle(command));
    }

    @Operation(summary = "Send an invitation link to the tenant of a store",
            description = "Answers 404 unit_not_found and 409 unit_not_available, common_area_without_tenant or invalid_email.")
    @PostMapping("/{unitId}/invite")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantInvitationResource inviteTenant(@PathVariable Long unitId,
                                                 @Valid @RequestBody InviteTenantResource resource) {
        var invitation = invitationCommandService.handle(new InviteTenantCommand(unitId,
                externalIamService.fetchAuthenticatedAdministratorId(), resource.email()));
        return TenantInvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
    }
}
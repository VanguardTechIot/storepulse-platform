package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.AcceptTenantInvitationCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RevokeTenantInvitationCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationByTokenQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationsByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.TenantInvitationCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.TenantInvitationQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.TenantInvitationResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.UpdateTenantInvitationResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.TenantInvitationResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tenant-invitations")
@Tag(name = "Tenant Invitations", description = "Invitation links sent to the tenants of the stores")
public class TenantInvitationsController {

    private final TenantInvitationCommandService invitationCommandService;
    private final TenantInvitationQueryService invitationQueryService;

    public TenantInvitationsController(TenantInvitationCommandService invitationCommandService,
                                       TenantInvitationQueryService invitationQueryService) {
        this.invitationCommandService = invitationCommandService;
        this.invitationQueryService = invitationQueryService;
    }

    @Operation(summary = "Get the invitations of a gallery, most recent first")
    @GetMapping
    public List<TenantInvitationResource> getInvitationsByGalleryId(@RequestParam Long galleryId) {
        return invitationQueryService.handle(new GetTenantInvitationsByGalleryIdQuery(galleryId)).stream()
                .map(TenantInvitationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }

    @Operation(summary = "Get an invitation by its link token")
    @GetMapping("/by-token/{token}")
    public TenantInvitationResource getInvitationByToken(@PathVariable String token) {
        return invitationQueryService.handle(new GetTenantInvitationByTokenQuery(token))
                .map(TenantInvitationResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.INVITATION_NOT_FOUND));
    }

    @Operation(summary = "Revoke a pending invitation (REVOKED) or close it once the tenant used it (ACCEPTED)",
            description = "Answers 409 invitation_not_pending or invitation_expired.")
    @PatchMapping("/{invitationId}")
    public TenantInvitationResource updateInvitation(@PathVariable Long invitationId,
                                                     @Valid @RequestBody UpdateTenantInvitationResource resource) {
        var invitation = switch (resource.status().trim().toUpperCase()) {
            case "REVOKED" -> invitationCommandService.handle(new RevokeTenantInvitationCommand(invitationId));
            case "ACCEPTED" -> invitationCommandService.handle(new AcceptTenantInvitationCommand(invitationId));
            default -> throw new IllegalArgumentException("Invitation status must be REVOKED or ACCEPTED");
        };
        return TenantInvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
    }
}

package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.AcceptTenantInvitationCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RevokeTenantInvitationCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationByTokenQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationsByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.TenantInvitationCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.TenantInvitationQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.AcceptTenantInvitationResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.TenantInvitationResource;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform.TenantInvitationResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tenant-invitations")
@Tag(name = "Tenant Invitations", description = "Invitation links sent to the tenants of the stores")
public class TenantInvitationsController {

    private final TenantInvitationCommandService invitationCommandService;
    private final TenantInvitationQueryService invitationQueryService;
    private final CommercialGalleryQueryService galleryQueryService;
    private final ExternalIamService externalIamService;

    public TenantInvitationsController(TenantInvitationCommandService invitationCommandService,
                                       TenantInvitationQueryService invitationQueryService,
                                       CommercialGalleryQueryService galleryQueryService,
                                       ExternalIamService externalIamService) {
        this.invitationCommandService = invitationCommandService;
        this.invitationQueryService = invitationQueryService;
        this.galleryQueryService = galleryQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get the invitations of the administrator's gallery, most recent first")
    @GetMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public List<TenantInvitationResource> getInvitationsOfAuthenticatedAdministrator() {
        var administratorId = externalIamService.fetchAuthenticatedAdministratorId();
        return galleryQueryService.handle(new GetCommercialGalleryByAdministratorIdQuery(administratorId))
                .map(gallery -> invitationQueryService.handle(new GetTenantInvitationsByGalleryIdQuery(gallery.getId())))
                .orElseGet(List::of).stream()
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

    @Operation(summary = "Accept an invitation with its link token; the tenant is the authenticated user",
            description = "Answers 404 invitation_not_found and 409 invitation_not_pending, invitation_expired, "
                    + "invitation_email_mismatch, unit_already_assigned or unit_not_available.")
    @PostMapping("/accept")
    @PreAuthorize("hasRole('TENANT')")
    public TenantInvitationResource acceptInvitation(@Valid @RequestBody AcceptTenantInvitationResource resource) {
        var invitation = invitationCommandService.handle(new AcceptTenantInvitationCommand(resource.token(),
                externalIamService.fetchAuthenticatedUserId()));
        return TenantInvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
    }

    @Operation(summary = "Revoke a pending invitation of the administrator's gallery",
            description = "Answers 404 invitation_not_found and 409 invitation_not_pending.")
    @PatchMapping("/{invitationId}/revoke")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public TenantInvitationResource revokeInvitation(@PathVariable Long invitationId) {
        var invitation = invitationCommandService.handle(new RevokeTenantInvitationCommand(invitationId,
                externalIamService.fetchAuthenticatedAdministratorId()));
        return TenantInvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
    }
}
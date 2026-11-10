package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalProfilesService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.AssignTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.TerminateTenantAssignmentCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetActiveAssignmentByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetAssignmentsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.AssignTenantResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.TenantAssignmentResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.TenantAssignmentResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tenant-assignments")
@Tag(name = "Tenant Assignments", description = "Which tenant occupies each store of the gallery")
public class TenantAssignmentsController {

    private final TenantAssignmentCommandService assignmentCommandService;
    private final TenantAssignmentQueryService assignmentQueryService;
    private final ExternalIamService externalIamService;
    private final ExternalProfilesService externalProfilesService;

    public TenantAssignmentsController(TenantAssignmentCommandService assignmentCommandService,
                                       TenantAssignmentQueryService assignmentQueryService, ExternalIamService externalIamService,
                                       ExternalProfilesService externalProfilesService) {
        this.assignmentCommandService = assignmentCommandService;
        this.assignmentQueryService = assignmentQueryService;
        this.externalIamService = externalIamService;
        this.externalProfilesService = externalProfilesService;
    }

    @Operation(summary = "Get the assignments of the authenticated user",
            description = "An administrator gets every assignment of the gallery, most recent first; a tenant gets "
                    + "their active one. Each one carries the tenant's name and email.")
    @GetMapping
    public List<TenantAssignmentResource> getAssignmentsOfAuthenticatedUser() {
        var userId = externalIamService.fetchAuthenticatedUserId();
        var assignments = externalIamService.isGalleryAdministrator(userId)
                ? assignmentQueryService.handle(new GetAssignmentsByAdministratorIdQuery(userId))
                : assignmentQueryService.handle(new GetActiveAssignmentByTenantIdQuery(userId)).stream().toList();
        return assignments.stream().map(this::toResource).toList();
    }

    @Operation(summary = "Assign a registered tenant to a free store of the administrator's gallery",
            description = "Answers 404 tenant_not_found or unit_not_found and 409 unit_already_assigned or unit_not_available.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantAssignmentResource assignTenant(@Valid @RequestBody AssignTenantResource resource) {
        var assignment = assignmentCommandService.handle(new AssignTenantCommand(resource.tenantId(),
                resource.commercialUnitId(), externalIamService.fetchAuthenticatedAdministratorId()));
        return toResource(assignment);
    }

    @Operation(summary = "Terminate an active assignment of the administrator; the store becomes available",
            description = "Answers 404 assignment_not_found and 409 assignment_not_active.")
    @PatchMapping("/{assignmentId}/terminate")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public TenantAssignmentResource terminateAssignment(@PathVariable Long assignmentId) {
        var command = new TerminateTenantAssignmentCommand(assignmentId, externalIamService.fetchAuthenticatedAdministratorId());
        return toResource(assignmentCommandService.handle(command));
    }

    private TenantAssignmentResource toResource(TenantAssignment assignment) {
        var tenantId = assignment.getTenantId();
        return TenantAssignmentResourceFromEntityAssembler.toResourceFromEntity(assignment,
                externalProfilesService.fetchFullName(tenantId).orElse(null),
                externalIamService.fetchEmail(tenantId).orElse(null));
    }
}
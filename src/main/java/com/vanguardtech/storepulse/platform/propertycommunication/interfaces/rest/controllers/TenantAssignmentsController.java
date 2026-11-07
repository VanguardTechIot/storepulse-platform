package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.AssignTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.TerminateTenantAssignmentCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetAssignmentsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.AssignTenantResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.TenantAssignmentResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.TenantAssignmentResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
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

    public TenantAssignmentsController(TenantAssignmentCommandService assignmentCommandService,
                                       TenantAssignmentQueryService assignmentQueryService, ExternalIamService externalIamService) {
        this.assignmentCommandService = assignmentCommandService;
        this.assignmentQueryService = assignmentQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get the assignments of an administrator, active and terminated, most recent first")
    @GetMapping
    public List<TenantAssignmentResource> getAssignmentsByAdministratorId(@RequestParam Long galleryAdministratorId) {
        return assignmentQueryService.handle(new GetAssignmentsByAdministratorIdQuery(galleryAdministratorId)).stream()
                .map(TenantAssignmentResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Assign a registered tenant to a free store",
            description = "Answers 404 tenant_not_found or unit_not_found and 409 unit_already_assigned or unit_not_available.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantAssignmentResource assignTenant(@Valid @RequestBody AssignTenantResource resource) {
        var administratorId = resource.galleryAdministratorId() != null ? resource.galleryAdministratorId()
                : externalIamService.fetchAuthenticatedAdministratorId()
                .orElseThrow(() -> new BusinessRuleException(PropertyCommunicationError.ADMINISTRATOR_NOT_AUTHENTICATED));
        var assignment = assignmentCommandService.handle(new AssignTenantCommand(resource.tenantId(),
                resource.commercialUnitId(), administratorId));
        return TenantAssignmentResourceFromEntityAssembler.toResourceFromEntity(assignment);
    }

    @Operation(summary = "Terminate an active assignment; the store becomes available",
            description = "Answers 409 assignment_not_active.")
    @PatchMapping("/{assignmentId}/terminate")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public TenantAssignmentResource terminateAssignment(@PathVariable Long assignmentId) {
        return TenantAssignmentResourceFromEntityAssembler.toResourceFromEntity(
                assignmentCommandService.handle(new TerminateTenantAssignmentCommand(assignmentId)));
    }
}

package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetCommunicationLogsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.CommunicationLogCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.CommunicationLogQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.CommunicationLogResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.RegisterCommunicationLogResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.CommunicationLogResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.RegisterCommunicationLogCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/communication-logs")
@Tag(name = "Communication Logs", description = "Trace of every communication with the tenants")
public class CommunicationLogsController {

    private final CommunicationLogCommandService logCommandService;
    private final CommunicationLogQueryService logQueryService;
    private final ExternalIamService externalIamService;

    public CommunicationLogsController(CommunicationLogCommandService logCommandService,
                                       CommunicationLogQueryService logQueryService, ExternalIamService externalIamService) {
        this.logCommandService = logCommandService;
        this.logQueryService = logQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get the communication logs of the authenticated administrator, most recent first")
    @GetMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public List<CommunicationLogResource> getLogsOfAuthenticatedAdministrator() {
        var administratorId = externalIamService.fetchAuthenticatedAdministratorId();
        return logQueryService.handle(new GetCommunicationLogsByAdministratorIdQuery(administratorId)).stream()
                .map(CommunicationLogResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Register a communication log entry")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunicationLogResource registerLog(@Valid @RequestBody RegisterCommunicationLogResource resource) {
        var command = RegisterCommunicationLogCommandFromResourceAssembler.toCommandFromResource(
                externalIamService.fetchAuthenticatedAdministratorId(), resource);
        return CommunicationLogResourceFromEntityAssembler.toResourceFromEntity(logCommandService.handle(command));
    }
}
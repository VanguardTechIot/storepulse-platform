package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetCommunicationLogsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.CommunicationLogCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.CommunicationLogQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.CommunicationLogResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.RegisterCommunicationLogResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.CommunicationLogResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.RegisterCommunicationLogCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
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

    @Operation(summary = "Get the communication logs of an administrator, most recent first")
    @GetMapping
    public List<CommunicationLogResource> getLogsByAdministratorId(@RequestParam Long galleryAdministratorId) {
        return logQueryService.handle(new GetCommunicationLogsByAdministratorIdQuery(galleryAdministratorId)).stream()
                .map(CommunicationLogResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Register a communication log entry")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public CommunicationLogResource registerLog(@Valid @RequestBody RegisterCommunicationLogResource resource) {
        var administratorId = resource.galleryAdministratorId() != null ? resource.galleryAdministratorId()
                : externalIamService.fetchAuthenticatedAdministratorId()
                .orElseThrow(() -> new BusinessRuleException(PropertyCommunicationError.ADMINISTRATOR_NOT_AUTHENTICATED));
        var command = RegisterCommunicationLogCommandFromResourceAssembler.toCommandFromResource(administratorId, resource);
        return CommunicationLogResourceFromEntityAssembler.toResourceFromEntity(logCommandService.handle(command));
    }
}

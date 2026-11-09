package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.MarkNotificationReadCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RetryNotificationCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetActiveAssignmentByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationsByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantAssignmentQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.NotifyTenantResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.PublishAnnouncementResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.TenantNotificationResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.NotifyTenantCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.PublishAnnouncementCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.TenantNotificationResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tenant-notifications")
@Tag(name = "Tenant Notifications", description = "Notifications, announcements and incident notices sent to tenants")
public class TenantNotificationsController {

    private final TenantNotificationCommandService notificationCommandService;
    private final TenantNotificationQueryService notificationQueryService;
    private final TenantAssignmentQueryService assignmentQueryService;
    private final ExternalIamService externalIamService;

    public TenantNotificationsController(TenantNotificationCommandService notificationCommandService,
                                         TenantNotificationQueryService notificationQueryService,
                                         TenantAssignmentQueryService assignmentQueryService,
                                         ExternalIamService externalIamService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
        this.assignmentQueryService = assignmentQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get the notifications sent by the authenticated administrator or received by the "
            + "authenticated tenant, most recent first")
    @GetMapping
    public List<TenantNotificationResource> getNotificationsOfAuthenticatedUser() {
        var userId = externalIamService.fetchAuthenticatedUserId();
        var notifications = externalIamService.isGalleryAdministrator(userId)
                ? notificationQueryService.handle(new GetNotificationsByAdministratorIdQuery(userId))
                : notificationQueryService.handle(new GetNotificationsByTenantIdQuery(userId));
        return notifications.stream().map(TenantNotificationResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Notify one tenant of the administrator's gallery",
            description = "Answers 404 tenant_not_found and 409 invalid_notification_title or invalid_notification_body.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantNotificationResource notifyTenant(@Valid @RequestBody NotifyTenantResource resource) {
        var administratorId = externalIamService.fetchAuthenticatedAdministratorId();
        assignmentQueryService.handle(new GetActiveAssignmentByTenantIdQuery(resource.tenantId()))
                .filter(assignment -> assignment.getGalleryAdministratorId().equals(administratorId))
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.TENANT_NOT_FOUND));
        var command = NotifyTenantCommandFromResourceAssembler.toCommandFromResource(administratorId, resource);
        return TenantNotificationResourceFromEntityAssembler.toResourceFromEntity(notificationCommandService.handle(command));
    }

    @Operation(summary = "Publish an announcement or incident notice to the tenants of the chosen stores",
            description = "Answers 409 no_recipients when no tenant occupies the stores.")
    @PostMapping("/announcements")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public List<TenantNotificationResource> publishAnnouncement(@Valid @RequestBody PublishAnnouncementResource resource) {
        var command = PublishAnnouncementCommandFromResourceAssembler.toCommandFromResource(
                externalIamService.fetchAuthenticatedAdministratorId(), resource);
        return notificationCommandService.handle(command).stream()
                .map(TenantNotificationResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Deliver again a failed notification of the administrator",
            description = "Answers 404 notification_not_found and 409 notification_not_failed.")
    @PatchMapping("/{notificationId}/retry")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public TenantNotificationResource retryNotification(@PathVariable Long notificationId) {
        var command = new RetryNotificationCommand(notificationId, externalIamService.fetchAuthenticatedAdministratorId());
        return TenantNotificationResourceFromEntityAssembler.toResourceFromEntity(notificationCommandService.handle(command));
    }

    @Operation(summary = "Mark a notification of the authenticated tenant as read",
            description = "Answers 404 notification_not_found.")
    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('TENANT')")
    public TenantNotificationResource markNotificationRead(@PathVariable Long notificationId) {
        var command = new MarkNotificationReadCommand(notificationId, externalIamService.fetchAuthenticatedUserId());
        return TenantNotificationResourceFromEntityAssembler.toResourceFromEntity(notificationCommandService.handle(command));
    }
}
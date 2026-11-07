package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.MarkNotificationReadCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RetryNotificationCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationsByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.NotifyTenantResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.PublishAnnouncementResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.TenantNotificationResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.UpdateTenantNotificationResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.NotifyTenantCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.PublishAnnouncementCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.TenantNotificationResourceFromEntityAssembler;
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
@RequestMapping("/tenant-notifications")
@Tag(name = "Tenant Notifications", description = "Notifications, announcements and incident notices sent to tenants")
public class TenantNotificationsController {

    private final TenantNotificationCommandService notificationCommandService;
    private final TenantNotificationQueryService notificationQueryService;
    private final ExternalIamService externalIamService;

    public TenantNotificationsController(TenantNotificationCommandService notificationCommandService,
                                         TenantNotificationQueryService notificationQueryService,
                                         ExternalIamService externalIamService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get the notifications sent by an administrator or received by a tenant, most recent first")
    @GetMapping
    public List<TenantNotificationResource> getNotifications(@RequestParam(required = false) Long galleryAdministratorId,
                                                             @RequestParam(required = false) Long tenantId) {
        if (galleryAdministratorId == null && tenantId == null)
            throw new IllegalArgumentException("galleryAdministratorId or tenantId is required");
        var notifications = galleryAdministratorId != null
                ? notificationQueryService.handle(new GetNotificationsByAdministratorIdQuery(galleryAdministratorId))
                : notificationQueryService.handle(new GetNotificationsByTenantIdQuery(tenantId));
        return notifications.stream().map(TenantNotificationResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Notify one tenant",
            description = "Answers 409 invalid_notification_title or invalid_notification_body.")
    @PostMapping
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public TenantNotificationResource notifyTenant(@Valid @RequestBody NotifyTenantResource resource) {
        var command = NotifyTenantCommandFromResourceAssembler.toCommandFromResource(administratorOf(resource.galleryAdministratorId()), resource);
        return TenantNotificationResourceFromEntityAssembler.toResourceFromEntity(notificationCommandService.handle(command));
    }

    @Operation(summary = "Publish an announcement or incident notice to the tenants of the chosen stores",
            description = "Answers 409 no_recipients when no tenant occupies the stores.")
    @PostMapping("/announcements")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public List<TenantNotificationResource> publishAnnouncement(@Valid @RequestBody PublishAnnouncementResource resource) {
        var command = PublishAnnouncementCommandFromResourceAssembler.toCommandFromResource(administratorOf(null), resource);
        return notificationCommandService.handle(command).stream()
                .map(TenantNotificationResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Retry a failed notification (status=PENDING) or mark it as read (read=true)",
            description = "Answers 409 notification_not_failed.")
    @PatchMapping("/{notificationId}")
    public TenantNotificationResource updateNotification(@PathVariable Long notificationId,
                                                         @RequestBody UpdateTenantNotificationResource resource) {
        TenantNotification notification = null;
        if ("PENDING".equalsIgnoreCase(resource.status()))
            notification = notificationCommandService.handle(new RetryNotificationCommand(notificationId));
        if (Boolean.TRUE.equals(resource.read()))
            notification = notificationCommandService.handle(new MarkNotificationReadCommand(notificationId));
        if (notification == null)
            notification = notificationQueryService.handle(new GetNotificationByIdQuery(notificationId))
                    .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.NOTIFICATION_NOT_FOUND));
        return TenantNotificationResourceFromEntityAssembler.toResourceFromEntity(notification);
    }

    private Long administratorOf(Long requested) {
        if (requested != null)
            return requested;
        return externalIamService.fetchAuthenticatedAdministratorId()
                .orElseThrow(() -> new BusinessRuleException(PropertyCommunicationError.ADMINISTRATOR_NOT_AUTHENTICATED));
    }
}

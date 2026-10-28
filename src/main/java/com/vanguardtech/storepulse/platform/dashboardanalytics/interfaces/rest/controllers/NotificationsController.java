package com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.MarkAllNotificationsAsReadCommand;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.commands.MarkNotificationAsReadCommand;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetAllNotificationsQuery;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.NotificationCommandService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.NotificationQueryService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.resources.NotificationResource;
import com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationsController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    @GetMapping
    public List<NotificationResource> getAllNotifications() {
        return notificationQueryService.handle(new GetAllNotificationsQuery()).stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResource markAsRead(@PathVariable Long notificationId) {
        return NotificationResourceFromEntityAssembler.toResourceFromEntity(
                notificationCommandService.handle(new MarkNotificationAsReadCommand(notificationId)));
    }

    @PatchMapping("/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllAsRead() {
        notificationCommandService.handle(new MarkAllNotificationsAsReadCommand());
    }
}
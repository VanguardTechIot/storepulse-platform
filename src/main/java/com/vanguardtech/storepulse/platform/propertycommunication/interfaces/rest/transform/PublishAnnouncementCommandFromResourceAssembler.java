package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.PublishAnnouncementCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationType;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.PublishAnnouncementResource;
import java.util.List;

public class PublishAnnouncementCommandFromResourceAssembler {
    public static PublishAnnouncementCommand toCommandFromResource(Long administratorId, PublishAnnouncementResource resource) {
        var type = Enums.parse(NotificationType.class, resource.type());
        if (type != NotificationType.ANNOUNCEMENT && type != NotificationType.INCIDENT)
            throw new IllegalArgumentException("An announcement is of type ANNOUNCEMENT or INCIDENT");
        return new PublishAnnouncementCommand(administratorId, type,
                resource.unitIds() == null ? List.of() : resource.unitIds(), resource.wholeGallery(), resource.title(),
                resource.body(), Enums.parseOrDefault(NotificationChannel.class, resource.channel(), NotificationChannel.IN_APP));
    }
}

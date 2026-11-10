package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RegisterCommunicationLogCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.CommunicationAction;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.ReferenceType;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.RegisterCommunicationLogResource;

public class RegisterCommunicationLogCommandFromResourceAssembler {
    public static RegisterCommunicationLogCommand toCommandFromResource(Long administratorId, RegisterCommunicationLogResource resource) {
        return new RegisterCommunicationLogCommand(resource.referenceId(), Enums.parse(ReferenceType.class, resource.referenceType()),
                Enums.parse(CommunicationAction.class, resource.action()), resource.tenantId(), administratorId,
                Enums.parseOrDefault(NotificationChannel.class, resource.channel(), NotificationChannel.IN_APP), resource.detail());
    }
}

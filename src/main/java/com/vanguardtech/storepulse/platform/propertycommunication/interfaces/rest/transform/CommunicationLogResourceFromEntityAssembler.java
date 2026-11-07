package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.CommunicationLog;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.CommunicationLogResource;

public class CommunicationLogResourceFromEntityAssembler {
    public static CommunicationLogResource toResourceFromEntity(CommunicationLog log) {
        return new CommunicationLogResource(log.getId(), log.getReferenceId(), log.getReferenceType().name(), log.getAction().name(),
                log.getTenantId(), log.getGalleryAdministratorId(), log.getChannel().name(), log.getDetail(), log.getLoggedAt());
    }
}

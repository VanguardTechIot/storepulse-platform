package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.CommunicationAction;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.ReferenceType;

/** Records a communication with a tenant for traceability. */
public record RegisterCommunicationLogCommand(Long referenceId, ReferenceType referenceType, CommunicationAction action,
                                              Long tenantId, Long galleryAdministratorId, NotificationChannel channel,
                                              String detail) {
}

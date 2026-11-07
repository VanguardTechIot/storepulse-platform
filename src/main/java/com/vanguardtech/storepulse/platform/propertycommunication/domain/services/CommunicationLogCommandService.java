package com.vanguardtech.storepulse.platform.propertycommunication.domain.services;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.CommunicationLog;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RegisterCommunicationLogCommand;

public interface CommunicationLogCommandService {
    CommunicationLog handle(RegisterCommunicationLogCommand command);
}

package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.CommunicationLog;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RegisterCommunicationLogCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.CommunicationLogCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.CommunicationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunicationLogCommandServiceImpl implements CommunicationLogCommandService {

    private final CommunicationLogRepository logRepository;

    public CommunicationLogCommandServiceImpl(CommunicationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    @Transactional
    public CommunicationLog handle(RegisterCommunicationLogCommand command) {
        return logRepository.save(new CommunicationLog(command.referenceId(), command.referenceType(), command.action(),
                command.tenantId(), command.galleryAdministratorId(), command.channel(), command.detail()));
    }
}

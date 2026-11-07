package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.CommunicationLog;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetCommunicationLogsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.CommunicationLogQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.CommunicationLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommunicationLogQueryServiceImpl implements CommunicationLogQueryService {

    private final CommunicationLogRepository logRepository;

    public CommunicationLogQueryServiceImpl(CommunicationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public List<CommunicationLog> handle(GetCommunicationLogsByAdministratorIdQuery query) {
        return logRepository.findByGalleryAdministratorIdOrderByLoggedAtDesc(query.galleryAdministratorId());
    }
}

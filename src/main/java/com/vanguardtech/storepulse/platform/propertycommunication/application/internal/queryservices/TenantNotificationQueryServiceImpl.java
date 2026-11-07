package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetNotificationsByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.TenantNotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TenantNotificationQueryServiceImpl implements TenantNotificationQueryService {

    private final TenantNotificationRepository notificationRepository;

    public TenantNotificationQueryServiceImpl(TenantNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<TenantNotification> handle(GetNotificationsByAdministratorIdQuery query) {
        return notificationRepository.findByGalleryAdministratorIdOrderByCreatedAtDesc(query.galleryAdministratorId());
    }

    @Override
    public List<TenantNotification> handle(GetNotificationsByTenantIdQuery query) {
        return notificationRepository.findByTenantIdOrderByCreatedAtDesc(query.tenantId());
    }

    @Override
    public Optional<TenantNotification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }
}

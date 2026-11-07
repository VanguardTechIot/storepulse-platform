package com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TenantNotificationRepository extends JpaRepository<TenantNotification, Long> {
    List<TenantNotification> findByGalleryAdministratorIdOrderByCreatedAtDesc(Long galleryAdministratorId);
    List<TenantNotification> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
}

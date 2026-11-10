package com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.CommunicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommunicationLogRepository extends JpaRepository<CommunicationLog, Long> {
    List<CommunicationLog> findByGalleryAdministratorIdOrderByLoggedAtDesc(Long galleryAdministratorId);
}

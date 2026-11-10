package com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByGalleryAdministratorIdOrderByLastMessageAtDesc(Long galleryAdministratorId);
    List<Conversation> findByTenantIdOrderByLastMessageAtDesc(Long tenantId);
}

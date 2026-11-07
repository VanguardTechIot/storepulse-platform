package com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);
}

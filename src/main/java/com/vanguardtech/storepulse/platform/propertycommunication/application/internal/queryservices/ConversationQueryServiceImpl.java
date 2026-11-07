package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.Conversation;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.entities.Message;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationsByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetMessagesByConversationIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.ConversationQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.ConversationRepository;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationQueryServiceImpl implements ConversationQueryService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationQueryServiceImpl(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Conversation> handle(GetConversationsByAdministratorIdQuery query) {
        return conversationRepository.findByGalleryAdministratorIdOrderByLastMessageAtDesc(query.galleryAdministratorId());
    }

    @Override
    public List<Conversation> handle(GetConversationsByTenantIdQuery query) {
        return conversationRepository.findByTenantIdOrderByLastMessageAtDesc(query.tenantId());
    }

    @Override
    public Optional<Conversation> handle(GetConversationByIdQuery query) {
        return conversationRepository.findById(query.conversationId());
    }

    @Override
    public List<Message> handle(GetMessagesByConversationIdQuery query) {
        return messageRepository.findByConversationIdOrderBySentAtAsc(query.conversationId());
    }
}

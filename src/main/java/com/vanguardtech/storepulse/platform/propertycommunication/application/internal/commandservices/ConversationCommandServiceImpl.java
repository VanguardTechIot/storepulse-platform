package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.Conversation;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.entities.Message;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.MarkConversationResolvedCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.RegisterCommunicationLogCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.SendMessageCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.StartConversationCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.CommunicationAction;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.NotificationChannel;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.ReferenceType;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.CommunicationLogCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.ConversationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.ConversationRepository;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.MessageRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.AssignmentStatus;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.TenantAssignmentRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversationCommandServiceImpl implements ConversationCommandService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final TenantAssignmentRepository assignmentRepository;
    private final CommunicationLogCommandService logCommandService;

    public ConversationCommandServiceImpl(ConversationRepository conversationRepository, MessageRepository messageRepository,
                                          TenantAssignmentRepository assignmentRepository,
                                          CommunicationLogCommandService logCommandService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.assignmentRepository = assignmentRepository;
        this.logCommandService = logCommandService;
    }

    @Override
    @Transactional
    public Conversation handle(StartConversationCommand command) {
        var tenantId = command.fromAdministrator() ? command.tenantId() : command.requesterId();
        var assignment = assignmentRepository.findFirstByTenantIdAndStatus(tenantId, AssignmentStatus.ACTIVE)
                .filter(active -> !command.fromAdministrator()
                        || active.getGalleryAdministratorId().equals(command.requesterId()))
                .orElseThrow(() -> new BusinessRuleException(PropertyCommunicationError.NO_ACTIVE_ASSIGNMENT));
        var conversation = conversationRepository.save(new Conversation(tenantId, assignment.getCommercialUnitId(),
                assignment.getGalleryAdministratorId(), command.subject()));
        logCommandService.handle(new RegisterCommunicationLogCommand(conversation.getId(), ReferenceType.CONVERSATION,
                CommunicationAction.CONVERSATION_STARTED, conversation.getTenantId(),
                conversation.getGalleryAdministratorId(), NotificationChannel.IN_APP, conversation.getSubject()));
        return conversation;
    }

    @Override
    @Transactional
    public Message handle(SendMessageCommand command) {
        var conversation = findConversation(command.conversationId());
        var senderRole = conversation.roleOf(command.senderId())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.CONVERSATION_NOT_FOUND));
        var message = conversation.writeMessage(command.senderId(), senderRole, command.content(), command.attachments());
        conversationRepository.save(conversation);
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public Conversation handle(MarkConversationResolvedCommand command) {
        var conversation = conversationRepository.findById(command.conversationId())
                .filter(found -> found.getGalleryAdministratorId().equals(command.administratorId()))
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.CONVERSATION_NOT_FOUND));
        conversation.markAsResolved();
        var saved = conversationRepository.save(conversation);
        logCommandService.handle(new RegisterCommunicationLogCommand(saved.getId(), ReferenceType.CONVERSATION,
                CommunicationAction.CONVERSATION_RESOLVED, saved.getTenantId(), saved.getGalleryAdministratorId(),
                NotificationChannel.IN_APP, saved.getSubject()));
        return saved;
    }

    private Conversation findConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.CONVERSATION_NOT_FOUND));
    }
}
package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.entities.Message;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.Attachment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.ConversationStatus;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.SenderRole;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;
import java.util.List;

/**
 * Conversation between a tenant and the gallery administrator (Aggregate Root). It owns its
 * messages: nothing can be sent once it is resolved, and the resolution date is kept.
 */
@Entity
@Table(name = "conversations")
@Getter
public class Conversation extends AuditableAbstractAggregateRoot<Conversation> {

    public static final int SUBJECT_MAX_LENGTH = 200;

    @Column(nullable = false)
    private Long tenantId;

    /** Store the conversation is about, when known; managed by Property Management. */
    private Long commercialUnitId;

    @Column(nullable = false)
    private Long galleryAdministratorId;

    @Column(nullable = false, length = SUBJECT_MAX_LENGTH)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status;

    @Column(nullable = false)
    private Instant startedAt;

    @Column(nullable = false)
    private Instant lastMessageAt;

    private Instant resolvedAt;

    protected Conversation() {
    }

    public Conversation(Long tenantId, Long commercialUnitId, Long galleryAdministratorId, String subject) {
        if (tenantId == null || tenantId <= 0)
            throw new IllegalArgumentException("Tenant id must be a positive number");
        if (galleryAdministratorId == null || galleryAdministratorId <= 0)
            throw new IllegalArgumentException("Gallery administrator id must be a positive number");
        if (!isValidSubject(subject))
            throw new BusinessRuleException(PropertyCommunicationError.INVALID_SUBJECT);
        this.tenantId = tenantId;
        this.commercialUnitId = commercialUnitId;
        this.galleryAdministratorId = galleryAdministratorId;
        this.subject = subject.trim();
        this.status = ConversationStatus.OPEN;
        this.startedAt = Instant.now();
        this.lastMessageAt = startedAt;
    }

    public static boolean isValidSubject(String subject) {
        return subject != null && !subject.isBlank() && subject.trim().length() <= SUBJECT_MAX_LENGTH;
    }

    public boolean isOpen() {
        return status == ConversationStatus.OPEN;
    }

    /**
     * Validates a new message of this conversation and moves the conversation to its date. The
     * message is persisted by the repository of messages, with this conversation's id.
     */
    public Message writeMessage(Long senderId, SenderRole senderRole, String content, List<Attachment> attachments) {
        if (!isOpen())
            throw new BusinessRuleException(PropertyCommunicationError.CONVERSATION_RESOLVED);
        var now = Instant.now();
        var message = new Message(getId(), senderId, senderRole, content, attachments, now);
        this.lastMessageAt = now;
        return message;
    }

    /** MarkConversationResolvedCommand: closes the conversation. */
    public void markAsResolved() {
        if (!isOpen())
            throw new BusinessRuleException(PropertyCommunicationError.CONVERSATION_RESOLVED);
        this.status = ConversationStatus.RESOLVED;
        this.resolvedAt = Instant.now();
    }
}

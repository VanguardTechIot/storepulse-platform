package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.entities;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.Attachment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.SenderRole;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Message of a conversation (Entity), written by the tenant or by the gallery administrator. It is
 * created through {@code Conversation.writeMessage}, which validates it.
 */
@Entity
@Table(name = "conversation_messages")
@Getter
public class Message {

    public static final int CONTENT_MAX_LENGTH = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long conversationId;

    @Column(nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderRole senderRole;

    @Column(nullable = false, length = CONTENT_MAX_LENGTH)
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"))
    private List<Attachment> attachments = new ArrayList<>();

    @Column(nullable = false)
    private Instant sentAt;

    protected Message() {
    }

    public Message(Long conversationId, Long senderId, SenderRole senderRole, String content, List<Attachment> attachments,
                   Instant sentAt) {
        if (senderId == null || senderId <= 0)
            throw new IllegalArgumentException("Sender id must be a positive number");
        if (senderRole == null)
            throw new IllegalArgumentException("Sender role is required");
        if (!isValidContent(content))
            throw new BusinessRuleException(PropertyCommunicationError.INVALID_MESSAGE_CONTENT);
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderRole = senderRole;
        this.content = content.trim();
        this.attachments = attachments == null ? new ArrayList<>() : new ArrayList<>(attachments);
        this.sentAt = sentAt;
    }

    public static boolean isValidContent(String content) {
        return content != null && !content.isBlank() && content.trim().length() <= CONTENT_MAX_LENGTH;
    }

    public boolean isFromAdministrator() {
        return senderRole == SenderRole.GALLERY_ADMINISTRATOR;
    }
}

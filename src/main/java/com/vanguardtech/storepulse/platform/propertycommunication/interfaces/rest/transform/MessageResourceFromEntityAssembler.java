package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.entities.Message;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.AttachmentResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.MessageResource;

public class MessageResourceFromEntityAssembler {
    public static MessageResource toResourceFromEntity(Message message) {
        var attachments = message.getAttachments().stream()
                .map(attachment -> new AttachmentResource(attachment.fileName(), attachment.url(), attachment.mimeType(),
                        attachment.sizeBytes()))
                .toList();
        return new MessageResource(message.getId(), message.getConversationId(), message.getSenderId(),
                message.getSenderRole().name(), message.getContent(), attachments, message.getSentAt());
    }
}

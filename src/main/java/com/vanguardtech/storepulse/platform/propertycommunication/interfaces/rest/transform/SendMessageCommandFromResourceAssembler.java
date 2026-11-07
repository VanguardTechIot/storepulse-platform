package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.SendMessageCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.Attachment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.SenderRole;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.SendMessageResource;
import java.util.List;

public class SendMessageCommandFromResourceAssembler {
    public static SendMessageCommand toCommandFromResource(Long conversationId, Long senderId, SendMessageResource resource) {
        var attachments = resource.attachments() == null ? List.<Attachment>of() : resource.attachments().stream()
                .map(attachment -> new Attachment(attachment.fileName(), attachment.url(), attachment.mimeType(),
                        attachment.sizeBytes()))
                .toList();
        return new SendMessageCommand(conversationId, senderId, Enums.parse(SenderRole.class, resource.senderRole()),
                resource.content(), attachments);
    }
}

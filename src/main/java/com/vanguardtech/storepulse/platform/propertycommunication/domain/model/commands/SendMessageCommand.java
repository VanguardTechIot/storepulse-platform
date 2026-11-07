package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.Attachment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.SenderRole;
import java.util.List;

/** Sends a message, with optional supporting documents, inside an open conversation. */
public record SendMessageCommand(Long conversationId, Long senderId, SenderRole senderRole, String content,
                                 List<Attachment> attachments) {
}

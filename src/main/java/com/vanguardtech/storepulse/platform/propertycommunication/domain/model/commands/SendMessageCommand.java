package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.Attachment;
import java.util.List;

/** Sends a message, with optional supporting documents, inside an open conversation of the sender. */
public record SendMessageCommand(Long conversationId, Long senderId, String content, List<Attachment> attachments) {
}
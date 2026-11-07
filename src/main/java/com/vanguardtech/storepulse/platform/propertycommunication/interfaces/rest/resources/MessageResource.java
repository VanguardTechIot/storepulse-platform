package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import java.time.Instant;
import java.util.List;

public record MessageResource(Long id, Long conversationId, Long senderId, String senderRole, String content,
                              List<AttachmentResource> attachments, Instant sentAt) {
}

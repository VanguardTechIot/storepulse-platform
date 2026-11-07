package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/** Closes a conversation; nothing can be sent afterwards. */
public record MarkConversationResolvedCommand(Long conversationId) {
}

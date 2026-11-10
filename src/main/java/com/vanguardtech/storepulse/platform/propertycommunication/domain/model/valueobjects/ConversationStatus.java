package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects;

/** Lifecycle of a conversation: no messages can be sent once it is resolved. */
public enum ConversationStatus {
    OPEN,
    RESOLVED
}

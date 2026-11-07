package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects;

/** What happened in a logged communication; the log detail holds its subject or title. */
public enum CommunicationAction {
    CONVERSATION_STARTED,
    CONVERSATION_RESOLVED,
    NOTIFICATION_SENT,
    NOTIFICATION_FAILED
}

package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects;

/** Reason of a tenant notification. ANNOUNCEMENT and INCIDENT are published by the administrator (US-34, US-35); the others are sent by the platform when something happens. */
public enum NotificationType {
    UTILITY_BILL_ISSUED,
    CONVERSATION_STARTED,
    MESSAGE_RECEIVED,
    ASSIGNMENT_CREATED,
    ANNOUNCEMENT,
    INCIDENT,
    EMERGENCY_ALERT
}

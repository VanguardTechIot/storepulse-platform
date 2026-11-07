package com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions;

/**
 * Business outcomes that prevent a Property Communication operation from completing. The lowercase
 * name of each value is the error code answered by the REST API and the i18n key of the web
 * application ({@code communication.errors.<code>}).
 */
public enum PropertyCommunicationError {
    INVALID_SUBJECT,
    INVALID_MESSAGE_CONTENT,
    INVALID_ATTACHMENT,
    ATTACHMENT_TOO_LARGE,
    /** The conversation is resolved, so it accepts no more messages. */
    CONVERSATION_RESOLVED,
    CONVERSATION_NOT_FOUND,
    TENANT_NOT_FOUND,
    UNIT_NOT_FOUND,
    /** The store is not active and available, or it is a common area. */
    UNIT_NOT_AVAILABLE,
    /** The store already has an active tenant assignment. */
    UNIT_ALREADY_ASSIGNED,
    ASSIGNMENT_NOT_FOUND,
    ASSIGNMENT_NOT_ACTIVE,
    INVALID_NOTIFICATION_TITLE,
    INVALID_NOTIFICATION_BODY,
    NOTIFICATION_NOT_FOUND,
    /** Only failed notifications can be retried. */
    NOTIFICATION_NOT_FAILED,
    /** An announcement needs at least one tenant to deliver it to. */
    NO_RECIPIENTS,
    /** The request does not come from an active gallery administrator. */
    ADMINISTRATOR_NOT_AUTHENTICATED
}

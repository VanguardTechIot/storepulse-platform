package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/** Delivers again a notification that failed. */
public record RetryNotificationCommand(Long notificationId) {
}

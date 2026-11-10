package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/** Delivers again a notification of the administrator that failed. */
public record RetryNotificationCommand(Long notificationId, Long administratorId) {
}
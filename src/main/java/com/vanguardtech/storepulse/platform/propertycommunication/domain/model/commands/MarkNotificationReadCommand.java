package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/** The tenant opened one of their notifications. */
public record MarkNotificationReadCommand(Long notificationId, Long tenantId) {
}
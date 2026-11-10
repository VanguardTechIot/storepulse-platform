package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/**
 * Starts a conversation about the active store of a tenant. An administrator names the tenant; a
 * tenant always talks with the administrator of the store they occupy.
 */
public record StartConversationCommand(Long requesterId, boolean fromAdministrator, Long tenantId, String subject) {
}
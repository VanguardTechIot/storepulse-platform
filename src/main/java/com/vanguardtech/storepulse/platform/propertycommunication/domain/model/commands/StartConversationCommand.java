package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands;

/** Starts a conversation between a tenant and the gallery administrator about a store. */
public record StartConversationCommand(Long tenantId, Long commercialUnitId, Long galleryAdministratorId, String subject) {
}

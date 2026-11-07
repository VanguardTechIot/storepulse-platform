package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.Conversation;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.ConversationResource;

public class ConversationResourceFromEntityAssembler {
    public static ConversationResource toResourceFromEntity(Conversation conversation) {
        return new ConversationResource(conversation.getId(), conversation.getTenantId(), conversation.getCommercialUnitId(),
                conversation.getGalleryAdministratorId(), conversation.getSubject(), conversation.getStatus().name(),
                conversation.getStartedAt(), conversation.getLastMessageAt(), conversation.getResolvedAt());
    }
}

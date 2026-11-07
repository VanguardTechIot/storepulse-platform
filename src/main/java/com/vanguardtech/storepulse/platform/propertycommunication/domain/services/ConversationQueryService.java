package com.vanguardtech.storepulse.platform.propertycommunication.domain.services;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.Conversation;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.entities.Message;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationsByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetMessagesByConversationIdQuery;
import java.util.List;
import java.util.Optional;

public interface ConversationQueryService {
    List<Conversation> handle(GetConversationsByAdministratorIdQuery query);
    List<Conversation> handle(GetConversationsByTenantIdQuery query);
    Optional<Conversation> handle(GetConversationByIdQuery query);
    List<Message> handle(GetMessagesByConversationIdQuery query);
}

package com.vanguardtech.storepulse.platform.propertycommunication.domain.services;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.Conversation;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.entities.Message;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.MarkConversationResolvedCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.SendMessageCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.StartConversationCommand;

public interface ConversationCommandService {
    Conversation handle(StartConversationCommand command);
    Message handle(SendMessageCommand command);
    Conversation handle(MarkConversationResolvedCommand command);
}

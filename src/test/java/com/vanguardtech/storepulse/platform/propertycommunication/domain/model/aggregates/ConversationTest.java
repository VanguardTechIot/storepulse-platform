package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.Attachment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.ConversationStatus;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.SenderRole;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ConversationTest {

    private static PropertyCommunicationError errorOf(Executable action) {
        var exception = assertThrows(BusinessRuleException.class, action);
        return PropertyCommunicationError.valueOf(exception.getMessage().toUpperCase());
    }

    @Test
    void startsOpenWithATrimmedSubject() {
        var conversation = new Conversation(5L, 3L, 7L, "  Goteo en el techo  ");

        assertEquals("Goteo en el techo", conversation.getSubject());
        assertEquals(ConversationStatus.OPEN, conversation.getStatus());
        assertEquals(conversation.getStartedAt(), conversation.getLastMessageAt());
        assertNull(conversation.getResolvedAt());
    }

    @Test
    void rejectsAnEmptyOrTooLongSubject() {
        assertEquals(PropertyCommunicationError.INVALID_SUBJECT, errorOf(() -> new Conversation(5L, 3L, 7L, " ")));
        assertEquals(PropertyCommunicationError.INVALID_SUBJECT, errorOf(() -> new Conversation(5L, 3L, 7L, "x".repeat(201))));
    }

    @Test
    void writesMessagesWithAttachmentsWhileOpen() {
        var conversation = new Conversation(5L, 3L, 7L, "Goteo en el techo");
        var attachment = new Attachment("foto.jpg", "https://files/foto.jpg", "image/jpeg", 1024L);

        var message = conversation.writeMessage(7L, SenderRole.GALLERY_ADMINISTRATOR, " Mañana va el técnico ", List.of(attachment));

        assertEquals("Mañana va el técnico", message.getContent());
        assertTrue(message.isFromAdministrator());
        assertEquals(1, message.getAttachments().size());
        assertEquals(message.getSentAt(), conversation.getLastMessageAt());
    }

    @Test
    void rejectsInvalidMessagesAndAttachments() {
        var conversation = new Conversation(5L, 3L, 7L, "Goteo en el techo");

        assertEquals(PropertyCommunicationError.INVALID_MESSAGE_CONTENT,
                errorOf(() -> conversation.writeMessage(5L, SenderRole.TENANT, "  ", List.of())));
        assertEquals(PropertyCommunicationError.INVALID_ATTACHMENT,
                errorOf(() -> new Attachment("virus.exe", "https://files/virus.exe", "application/octet-stream", 10L)));
        assertEquals(PropertyCommunicationError.ATTACHMENT_TOO_LARGE,
                errorOf(() -> new Attachment("foto.jpg", "https://files/foto.jpg", "image/jpeg", Attachment.MAX_SIZE_BYTES + 1)));
    }

    @Test
    void acceptsNothingOnceResolved() {
        var conversation = new Conversation(5L, 3L, 7L, "Goteo en el techo");
        conversation.markAsResolved();

        assertEquals(ConversationStatus.RESOLVED, conversation.getStatus());
        assertNotNull(conversation.getResolvedAt());
        assertEquals(PropertyCommunicationError.CONVERSATION_RESOLVED,
                errorOf(() -> conversation.writeMessage(5L, SenderRole.TENANT, "Hola", List.of())));
        assertEquals(PropertyCommunicationError.CONVERSATION_RESOLVED, errorOf(conversation::markAsResolved));
    }
}

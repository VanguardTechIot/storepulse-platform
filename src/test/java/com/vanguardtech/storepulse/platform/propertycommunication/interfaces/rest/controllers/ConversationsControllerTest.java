package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.Conversation;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.SendMessageCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.StartConversationCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.ConversationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.ConversationQueryService;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ConversationsControllerTest.MethodSecurity.class)
class ConversationsControllerTest {

    private static final String MESSAGE_REQUEST =
            "{\"senderId\":7,\"senderRole\":\"GALLERY_ADMINISTRATOR\",\"content\":\"Mañana va el técnico\",\"attachments\":[]}";

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurity {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationCommandService conversationCommandService;

    @MockitoBean
    private ConversationQueryService conversationQueryService;

    @MockitoBean
    private ExternalIamService externalIamService;

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void startsAConversationForTheAuthenticatedAdministrator() throws Exception {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(Optional.of(7L));
        when(conversationCommandService.handle(any(StartConversationCommand.class)))
                .thenReturn(new Conversation(5L, 3L, 7L, "Goteo en el techo"));

        mockMvc.perform(post("/api/v1/conversations").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenantId\":5,\"commercialUnitId\":3,\"subject\":\"Goteo en el techo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.galleryAdministratorId").value(7));
    }

    @Test
    @WithMockUser(roles = "GALLERY_ADMINISTRATOR")
    void answers409WhenTheConversationIsResolved() throws Exception {
        when(conversationCommandService.handle(any(SendMessageCommand.class)))
                .thenThrow(new BusinessRuleException(PropertyCommunicationError.CONVERSATION_RESOLVED));

        mockMvc.perform(post("/api/v1/conversations/1/messages").contentType(MediaType.APPLICATION_JSON).content(MESSAGE_REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("conversation_resolved"));
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void forbidsATenantFromResolvingConversations() throws Exception {
        mockMvc.perform(patch("/api/v1/conversations/1/resolve"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TENANT")
    void answers404ForAnUnknownConversation() throws Exception {
        when(conversationQueryService.handle(any(GetConversationByIdQuery.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/conversations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("conversation_not_found"));
    }
}

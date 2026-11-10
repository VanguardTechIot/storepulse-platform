package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.Conversation;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.MarkConversationResolvedCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.StartConversationCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationsByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetConversationsByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetMessagesByConversationIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.ConversationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.ConversationQueryService;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.ConversationResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.MessageResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.SendMessageResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources.StartConversationResource;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.ConversationResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.MessageResourceFromEntityAssembler;
import com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform.SendMessageCommandFromResourceAssembler;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/conversations")
@Tag(name = "Conversations", description = "Documented conversations between the administration and the tenants")
public class ConversationsController {

    private final ConversationCommandService conversationCommandService;
    private final ConversationQueryService conversationQueryService;
    private final ExternalIamService externalIamService;

    public ConversationsController(ConversationCommandService conversationCommandService,
                                   ConversationQueryService conversationQueryService, ExternalIamService externalIamService) {
        this.conversationCommandService = conversationCommandService;
        this.conversationQueryService = conversationQueryService;
        this.externalIamService = externalIamService;
    }

    @Operation(summary = "Get the conversations of the authenticated user, most recent first")
    @GetMapping
    public List<ConversationResource> getConversationsOfAuthenticatedUser() {
        var userId = externalIamService.fetchAuthenticatedUserId();
        var conversations = externalIamService.isGalleryAdministrator(userId)
                ? conversationQueryService.handle(new GetConversationsByAdministratorIdQuery(userId))
                : conversationQueryService.handle(new GetConversationsByTenantIdQuery(userId));
        return conversations.stream().map(ConversationResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Get a conversation of the authenticated user by id")
    @GetMapping("/{conversationId}")
    public ConversationResource getConversationById(@PathVariable Long conversationId) {
        return ConversationResourceFromEntityAssembler.toResourceFromEntity(findOwnConversation(conversationId));
    }

    @Operation(summary = "Start a conversation about the active store of a tenant",
            description = "An administrator sends tenantId; a tenant leaves it out. Answers 409 invalid_subject or "
                    + "no_active_assignment.")
    @PostMapping
    @PreAuthorize("hasAnyRole('GALLERY_ADMINISTRATOR', 'TENANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResource startConversation(@Valid @RequestBody StartConversationResource resource) {
        var userId = externalIamService.fetchAuthenticatedUserId();
        var fromAdministrator = externalIamService.isGalleryAdministrator(userId);
        if (fromAdministrator && resource.tenantId() == null)
            throw new IllegalArgumentException("tenantId is required");
        var conversation = conversationCommandService.handle(new StartConversationCommand(userId, fromAdministrator,
                resource.tenantId(), resource.subject()));
        return ConversationResourceFromEntityAssembler.toResourceFromEntity(conversation);
    }

    @Operation(summary = "Mark a conversation of the administrator as resolved",
            description = "Answers 404 conversation_not_found and 409 conversation_resolved.")
    @PatchMapping("/{conversationId}/resolve")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public ConversationResource resolveConversation(@PathVariable Long conversationId) {
        var command = new MarkConversationResolvedCommand(conversationId, externalIamService.fetchAuthenticatedAdministratorId());
        return ConversationResourceFromEntityAssembler.toResourceFromEntity(conversationCommandService.handle(command));
    }

    @Operation(summary = "Get the messages of a conversation of the authenticated user, oldest first")
    @GetMapping("/{conversationId}/messages")
    public List<MessageResource> getMessages(@PathVariable Long conversationId) {
        findOwnConversation(conversationId);
        return conversationQueryService.handle(new GetMessagesByConversationIdQuery(conversationId)).stream()
                .map(MessageResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Send a message with optional attachments; the sender is the authenticated user",
            description = "Answers 404 conversation_not_found and 409 conversation_resolved, invalid_message_content, "
                    + "invalid_attachment or attachment_too_large.")
    @PostMapping("/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResource sendMessage(@PathVariable Long conversationId, @Valid @RequestBody SendMessageResource resource) {
        var command = SendMessageCommandFromResourceAssembler.toCommandFromResource(conversationId,
                externalIamService.fetchAuthenticatedUserId(), resource);
        return MessageResourceFromEntityAssembler.toResourceFromEntity(conversationCommandService.handle(command));
    }

    /** A conversation of other users is answered as not found. */
    private Conversation findOwnConversation(Long conversationId) {
        var userId = externalIamService.fetchAuthenticatedUserId();
        return conversationQueryService.handle(new GetConversationByIdQuery(conversationId))
                .filter(conversation -> conversation.roleOf(userId).isPresent())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.CONVERSATION_NOT_FOUND));
    }
}
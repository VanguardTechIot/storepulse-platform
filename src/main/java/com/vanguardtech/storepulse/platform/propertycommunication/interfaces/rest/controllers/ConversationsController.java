package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
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
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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

    @Operation(summary = "Get the conversations of a gallery administrator, most recent first")
    @GetMapping("/administrators/{administratorId}")
    public List<ConversationResource> getConversationsByAdministratorId(@PathVariable Long administratorId) {
        return conversationQueryService.handle(new GetConversationsByAdministratorIdQuery(administratorId)).stream()
                .map(ConversationResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Get the conversations of a tenant, most recent first")
    @GetMapping("/tenants/{tenantId}")
    public List<ConversationResource> getConversationsByTenantId(@PathVariable Long tenantId) {
        return conversationQueryService.handle(new GetConversationsByTenantIdQuery(tenantId)).stream()
                .map(ConversationResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Get a conversation by id")
    @GetMapping("/{conversationId}")
    public ConversationResource getConversationById(@PathVariable Long conversationId) {
        return conversationQueryService.handle(new GetConversationByIdQuery(conversationId))
                .map(ConversationResourceFromEntityAssembler::toResourceFromEntity)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.CONVERSATION_NOT_FOUND));
    }

    @Operation(summary = "Start a conversation", description = "Answers 409 invalid_subject.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResource startConversation(@Valid @RequestBody StartConversationResource resource) {
        var administratorId = resource.galleryAdministratorId() != null ? resource.galleryAdministratorId()
                : externalIamService.fetchAuthenticatedAdministratorId()
                .orElseThrow(() -> new BusinessRuleException(PropertyCommunicationError.ADMINISTRATOR_NOT_AUTHENTICATED));
        var conversation = conversationCommandService.handle(new StartConversationCommand(resource.tenantId(),
                resource.commercialUnitId(), administratorId, resource.subject()));
        return ConversationResourceFromEntityAssembler.toResourceFromEntity(conversation);
    }

    @Operation(summary = "Mark a conversation as resolved", description = "Answers 409 conversation_resolved.")
    @PatchMapping("/{conversationId}/resolve")
    @PreAuthorize("hasRole('GALLERY_ADMINISTRATOR')")
    public ConversationResource resolveConversation(@PathVariable Long conversationId) {
        return ConversationResourceFromEntityAssembler.toResourceFromEntity(
                conversationCommandService.handle(new MarkConversationResolvedCommand(conversationId)));
    }

    @Operation(summary = "Get the messages of a conversation, oldest first")
    @GetMapping("/{conversationId}/messages")
    public List<MessageResource> getMessages(@PathVariable Long conversationId) {
        conversationQueryService.handle(new GetConversationByIdQuery(conversationId))
                .orElseThrow(() -> new ResourceNotFoundException(PropertyCommunicationError.CONVERSATION_NOT_FOUND));
        return conversationQueryService.handle(new GetMessagesByConversationIdQuery(conversationId)).stream()
                .map(MessageResourceFromEntityAssembler::toResourceFromEntity).toList();
    }

    @Operation(summary = "Send a message with optional attachments",
            description = "Answers 409 conversation_resolved, invalid_message_content, invalid_attachment or attachment_too_large.")
    @PostMapping("/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResource sendMessage(@PathVariable Long conversationId, @Valid @RequestBody SendMessageResource resource) {
        var senderId = resource.senderId() != null ? resource.senderId()
                : externalIamService.fetchAuthenticatedUserId().orElseThrow(() -> new AccessDeniedException("No sender"));
        var command = SendMessageCommandFromResourceAssembler.toCommandFromResource(conversationId, senderId, resource);
        return MessageResourceFromEntityAssembler.toResourceFromEntity(conversationCommandService.handle(command));
    }
}

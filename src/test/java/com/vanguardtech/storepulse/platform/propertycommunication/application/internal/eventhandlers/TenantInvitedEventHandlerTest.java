package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events.TenantInvitedEvent;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.CommercialUnitSummary;
import com.vanguardtech.storepulse.platform.shared.application.outboundservices.EmailSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TenantInvitedEventHandlerTest {

    @Test
    void emailsTheInvitationLinkToTheInvitedTenant() {
        var emailSender = mock(EmailSender.class);
        var propertyManagement = mock(ExternalPropertyManagementService.class);
        when(propertyManagement.fetchUnit(3L)).thenReturn(Optional.of(
                new CommercialUnitSummary(3L, 1L, "A-03", "1", "Tienda Sol", true, true, true)));
        var handler = new TenantInvitedEventHandler(emailSender, propertyManagement, "http://localhost:4200/invitations/accept");

        handler.on(new TenantInvitedEvent(9L, 1L, 3L, "t1@x.com", "abc123", Instant.now()));

        var text = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(eq("t1@x.com"), eq("Invitación a StorePulse"), text.capture());
        assertTrue(text.getValue().contains("A-03"));
        assertTrue(text.getValue().contains("http://localhost:4200/invitations/accept?token=abc123"));
    }
}
package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events.TenantInvitedEvent;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.CommercialUnitSummary;
import com.vanguardtech.storepulse.platform.shared.application.outboundservices.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Emails the invitation link to the invited tenant when Property Management issues an invitation.
 * It runs in the same transaction, so the invitation is not kept when the email cannot be sent.
 */
@Service
public class TenantInvitedEventHandler {

    private static final String SUBJECT = "Invitación a StorePulse";
    private static final String TEXT = """
            Te invitaron a ocupar el local %s en StorePulse.

            Regístrate como inquilino con este correo y acepta la invitación aquí:
            %s?token=%s

            La invitación vence en una semana. Si no la esperabas, ignora este mensaje.""";

    private final EmailSender emailSender;
    private final ExternalPropertyManagementService externalPropertyManagementService;
    private final String invitationUrl;

    public TenantInvitedEventHandler(EmailSender emailSender,
                                     ExternalPropertyManagementService externalPropertyManagementService,
                                     @Value("${storepulse.web.invitation-url}") String invitationUrl) {
        this.emailSender = emailSender;
        this.externalPropertyManagementService = externalPropertyManagementService;
        this.invitationUrl = invitationUrl;
    }

    @EventListener
    public void on(TenantInvitedEvent event) {
        var unitCode = externalPropertyManagementService.fetchUnit(event.unitId())
                .map(CommercialUnitSummary::code)
                .orElse("");
        emailSender.send(event.email(), SUBJECT, TEXT.formatted(unitCode, invitationUrl, event.token()));
    }
}
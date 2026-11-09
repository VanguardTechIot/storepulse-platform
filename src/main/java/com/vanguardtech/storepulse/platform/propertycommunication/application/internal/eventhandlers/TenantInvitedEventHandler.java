package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events.TenantInvitedEvent;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.CommercialUnitSummary;
import com.vanguardtech.storepulse.platform.shared.application.outboundservices.EmailSender;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Emails the invitation code to the invited tenant when Property Management issues an invitation;
 * the tenant enters it in the mobile application to accept.
 * It runs in the same transaction, so the invitation is not kept when the email cannot be sent.
 */
@Service
public class TenantInvitedEventHandler {

    private static final String SUBJECT = "Invitación a StorePulse";
    private static final String TEXT = """
            Te invitaron a ocupar el local %s en StorePulse.

            Descarga la aplicación móvil de StorePulse, regístrate como inquilino con este correo
            e ingresa este código de invitación:

            %s

            La invitación vence en una semana. Si no la esperabas, ignora este mensaje.""";

    private final EmailSender emailSender;
    private final ExternalPropertyManagementService externalPropertyManagementService;

    public TenantInvitedEventHandler(EmailSender emailSender,
                                     ExternalPropertyManagementService externalPropertyManagementService) {
        this.emailSender = emailSender;
        this.externalPropertyManagementService = externalPropertyManagementService;
    }

    @EventListener
    public void on(TenantInvitedEvent event) {
        var unitCode = externalPropertyManagementService.fetchUnit(event.unitId())
                .map(CommercialUnitSummary::code)
                .orElse("");
        emailSender.send(event.email(), SUBJECT, TEXT.formatted(unitCode, event.token()));
    }
}
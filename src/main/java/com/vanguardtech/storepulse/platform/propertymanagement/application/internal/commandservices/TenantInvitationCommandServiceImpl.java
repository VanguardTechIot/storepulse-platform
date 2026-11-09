package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.TenantInvitation;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.AcceptTenantInvitationCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.InviteTenantCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RevokeTenantInvitationCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.InvitationStatus;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.TenantInvitationCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.CommercialUnitRepository;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.TenantInvitationRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class TenantInvitationCommandServiceImpl implements TenantInvitationCommandService {

    private final TenantInvitationRepository invitationRepository;
    private final CommercialUnitRepository unitRepository;
    private final ExternalIamService externalIamService;

    public TenantInvitationCommandServiceImpl(TenantInvitationRepository invitationRepository,
                                              CommercialUnitRepository unitRepository,
                                              ExternalIamService externalIamService) {
        this.invitationRepository = invitationRepository;
        this.unitRepository = unitRepository;
        this.externalIamService = externalIamService;
    }

    /** A new invitation replaces any pending one of the same store. */
    @Override
    @Transactional
    public TenantInvitation handle(InviteTenantCommand command) {
        var unit = unitRepository.findByIdAndAdministratorId(command.unitId(), command.administratorId())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.UNIT_NOT_FOUND));
        invitationRepository.findByUnitIdAndStatus(unit.getId(), InvitationStatus.PENDING)
                .forEach(previous -> {
                    previous.revoke();
                    invitationRepository.save(previous);
                });
        var invitation = invitationRepository.save(new TenantInvitation(unit, command.email()));
        invitation.confirmIssued();
        return invitationRepository.save(invitation);
    }

    @Override
    @Transactional
    public TenantInvitation handle(RevokeTenantInvitationCommand command) {
        var invitation = invitationRepository.findByIdAndAdministratorId(command.invitationId(), command.administratorId())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.INVITATION_NOT_FOUND));
        invitation.revoke();
        return invitationRepository.save(invitation);
    }

    /** Property Communication assigns the tenant when the acceptance event is published on save. */
    @Override
    @Transactional
    public TenantInvitation handle(AcceptTenantInvitationCommand command) {
        var invitation = invitationRepository.findByToken(command.token())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.INVITATION_NOT_FOUND));
        var tenantEmail = externalIamService.fetchEmailByUserId(command.tenantId()).orElse(null);
        invitation.accept(command.tenantId(), tenantEmail, Instant.now());
        return invitationRepository.save(invitation);
    }
}
package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.commandservices;

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

    public TenantInvitationCommandServiceImpl(TenantInvitationRepository invitationRepository,
                                              CommercialUnitRepository unitRepository) {
        this.invitationRepository = invitationRepository;
        this.unitRepository = unitRepository;
    }

    /** US-12: a new invitation replaces any pending one of the same store. */
    @Override
    @Transactional
    public TenantInvitation handle(InviteTenantCommand command) {
        var unit = unitRepository.findById(command.unitId())
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
        var invitation = findInvitation(command.invitationId());
        invitation.revoke();
        return invitationRepository.save(invitation);
    }

    /** The tenant registered with the link: the invitation closes and the store becomes occupied. */
    @Override
    @Transactional
    public TenantInvitation handle(AcceptTenantInvitationCommand command) {
        var invitation = findInvitation(command.invitationId());
        invitation.accept(Instant.now());
        unitRepository.findById(invitation.getUnitId()).ifPresent(unit -> {
            unit.assignTenant();
            unitRepository.save(unit);
        });
        return invitationRepository.save(invitation);
    }

    private TenantInvitation findInvitation(Long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.INVITATION_NOT_FOUND));
    }
}

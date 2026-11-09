package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertymanagement.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.TenantInvitation;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.AcceptTenantInvitationCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.InviteTenantCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.InvitationStatus;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitStatus;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitType;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.CommercialUnitRepository;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.TenantInvitationRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantInvitationCommandServiceImplTest {

    @Mock
    private TenantInvitationRepository invitationRepository;
    @Mock
    private CommercialUnitRepository unitRepository;
    @Mock
    private ExternalIamService externalIamService;

    @InjectMocks
    private TenantInvitationCommandServiceImpl service;

    @Test
    void refusesToInviteToAStoreOfAnotherAdministrator() {
        when(unitRepository.findByIdAndAdministratorId(3L, 8L)).thenReturn(Optional.empty());

        var error = assertThrows(ResourceNotFoundException.class,
                () -> service.handle(new InviteTenantCommand(3L, 8L, "carlos.rojas@gmail.com")));

        assertEquals("unit_not_found", error.getMessage());
        verify(invitationRepository, never()).save(any());
    }

    @Test
    void acceptingLeavesTheStoreToTheTenantAssignment() {
        var store = new CommercialUnit(1L, UnitType.STORE, "A-10", "1", 20.0, null);
        var invitation = new TenantInvitation(store, "carlos.rojas@gmail.com");
        when(invitationRepository.findByToken("abc")).thenReturn(Optional.of(invitation));
        when(externalIamService.fetchEmailByUserId(5L)).thenReturn(Optional.of("carlos.rojas@gmail.com"));
        when(invitationRepository.save(any(TenantInvitation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var accepted = service.handle(new AcceptTenantInvitationCommand("abc", 5L));

        assertEquals(InvitationStatus.ACCEPTED, accepted.getStatus());
        assertEquals(UnitStatus.AVAILABLE, store.getStatus());
        verifyNoInteractions(unitRepository);
    }
}
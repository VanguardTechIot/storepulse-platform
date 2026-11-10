package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices.acl.ExternalPropertyManagementService;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.AssignTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.NotifyTenantCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.commands.TerminateTenantAssignmentCommand;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.AssignmentStatus;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.services.TenantNotificationCommandService;
import com.vanguardtech.storepulse.platform.propertycommunication.infrastructure.persistence.jpa.repositories.TenantAssignmentRepository;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.CommercialUnitSummary;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TenantAssignmentCommandServiceImplTest {

    @Mock
    private TenantAssignmentRepository assignmentRepository;
    @Mock
    private ExternalIamService externalIamService;
    @Mock
    private ExternalPropertyManagementService externalPropertyManagementService;
    @Mock
    private TenantNotificationCommandService notificationCommandService;

    @InjectMocks
    private TenantAssignmentCommandServiceImpl service;

    private static final CommercialUnitSummary FREE_STORE = new CommercialUnitSummary(3L, 1L, "A-03", "1", null, true, true, true);

    @Test
    void assignsATenantAndMarksTheStoreOccupied() {
        when(externalIamService.existsUser(5L)).thenReturn(true);
        when(externalPropertyManagementService.fetchUnit(3L)).thenReturn(Optional.of(FREE_STORE));
        when(assignmentRepository.findByCommercialUnitIdAndStatus(3L, AssignmentStatus.ACTIVE)).thenReturn(Optional.empty());
        when(externalPropertyManagementService.belongsToAdministrator(1L, 7L)).thenReturn(true);
        when(assignmentRepository.save(any(TenantAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var assignment = service.handle(new AssignTenantCommand(5L, 3L, 7L));

        assertTrue(assignment.isActive());
        verify(externalPropertyManagementService).markUnitOccupied(3L);
        verify(notificationCommandService).handle(any(NotifyTenantCommand.class));
    }

    @Test
    void refusesAStoreThatAlreadyHasATenant() {
        when(externalIamService.existsUser(5L)).thenReturn(true);
        when(externalPropertyManagementService.fetchUnit(3L)).thenReturn(Optional.of(FREE_STORE));
        when(assignmentRepository.findByCommercialUnitIdAndStatus(3L, AssignmentStatus.ACTIVE))
                .thenReturn(Optional.of(new TenantAssignment(9L, 3L, 7L)));

        var error = assertThrows(BusinessRuleException.class, () -> service.handle(new AssignTenantCommand(5L, 3L, 7L)));

        assertEquals("unit_already_assigned", error.getMessage());
        verify(externalPropertyManagementService, never()).markUnitOccupied(any());
    }

    @Test
    void refusesAStoreOfAnotherAdministrator() {
        when(externalIamService.existsUser(5L)).thenReturn(true);
        when(externalPropertyManagementService.fetchUnit(3L)).thenReturn(Optional.of(FREE_STORE));
        when(assignmentRepository.findByCommercialUnitIdAndStatus(3L, AssignmentStatus.ACTIVE)).thenReturn(Optional.empty());
        when(externalPropertyManagementService.belongsToAdministrator(1L, 8L)).thenReturn(false);

        var error = assertThrows(ResourceNotFoundException.class, () -> service.handle(new AssignTenantCommand(5L, 3L, 8L)));

        assertEquals("unit_not_found", error.getMessage());
        verify(externalPropertyManagementService, never()).markUnitOccupied(any());
    }

    @Test
    void refusesACommonAreaAndAnUnknownTenant() {
        when(externalIamService.existsUser(5L)).thenReturn(true);
        when(externalPropertyManagementService.fetchUnit(4L))
                .thenReturn(Optional.of(new CommercialUnitSummary(4L, 1L, "Pasillo", "1", null, false, true, true)));
        when(assignmentRepository.findByCommercialUnitIdAndStatus(4L, AssignmentStatus.ACTIVE)).thenReturn(Optional.empty());
        when(externalPropertyManagementService.belongsToAdministrator(1L, 7L)).thenReturn(true);
        assertEquals("unit_not_available",
                assertThrows(BusinessRuleException.class, () -> service.handle(new AssignTenantCommand(5L, 4L, 7L))).getMessage());

        when(externalIamService.existsUser(99L)).thenReturn(false);
        assertEquals("tenant_not_found",
                assertThrows(ResourceNotFoundException.class, () -> service.handle(new AssignTenantCommand(99L, 3L, 7L))).getMessage());
    }

    @Test
    void terminatingFreesTheStore() {
        var assignment = new TenantAssignment(5L, 3L, 7L);
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(TenantAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals("assignment_not_found",
                assertThrows(ResourceNotFoundException.class, () -> service.handle(new TerminateTenantAssignmentCommand(1L, 8L))).getMessage());
        var terminated = service.handle(new TerminateTenantAssignmentCommand(1L, 7L));

        assertFalse(terminated.isActive());
        assertNotNull(terminated.getTerminatedAt());
        verify(externalPropertyManagementService).markUnitAvailable(3L);
        assertEquals("assignment_not_active",
                assertThrows(BusinessRuleException.class, () -> service.handle(new TerminateTenantAssignmentCommand(1L, 7L))).getMessage());
    }
}
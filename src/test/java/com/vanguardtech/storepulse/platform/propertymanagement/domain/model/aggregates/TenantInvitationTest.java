package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.InvitationStatus;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitType;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

class TenantInvitationTest {

    private static CommercialUnit store() {
        return new CommercialUnit(1L, UnitType.STORE, "A-10", "1", 20.0, null);
    }

    private static PropertyManagementError errorOf(Executable action) {
        var exception = assertThrows(BusinessRuleException.class, action);
        return PropertyManagementError.valueOf(exception.getMessage().toUpperCase());
    }

    @Test
    void issuesAPendingLinkValidForSevenDays() {
        var invitation = new TenantInvitation(store(), " Carlos.Rojas@Gmail.com ");

        assertEquals("carlos.rojas@gmail.com", invitation.getEmailAddress());
        assertTrue(invitation.getToken().matches("[0-9a-f]{16}"));
        assertEquals(InvitationStatus.PENDING, invitation.statusAt(Instant.now()));
        assertEquals(invitation.getSentAt().plus(7, ChronoUnit.DAYS), invitation.getExpiresAt());
    }

    @Test
    void expiresAfterSevenDaysAndCannotBeAcceptedThen() {
        var invitation = new TenantInvitation(store(), "carlos.rojas@gmail.com");
        var later = invitation.getExpiresAt().plusSeconds(1);

        assertEquals(InvitationStatus.EXPIRED, invitation.statusAt(later));
        assertEquals(PropertyManagementError.INVITATION_EXPIRED, errorOf(() -> invitation.accept(5L, "carlos.rojas@gmail.com", later)));
    }

    @Test
    void isAcceptedOnlyByTheInvitedEmail() {
        var invitation = new TenantInvitation(store(), "carlos.rojas@gmail.com");

        assertEquals(PropertyManagementError.INVITATION_EMAIL_MISMATCH,
                errorOf(() -> invitation.accept(6L, "otra.persona@gmail.com", Instant.now())));
        invitation.accept(5L, "Carlos.Rojas@gmail.com", Instant.now());
        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
    }

    @Test
    void isRevokedOnlyWhilePending() {
        var invitation = new TenantInvitation(store(), "carlos.rojas@gmail.com");
        invitation.revoke();

        assertEquals(InvitationStatus.REVOKED, invitation.statusAt(Instant.now()));
        assertEquals(PropertyManagementError.INVITATION_NOT_PENDING, errorOf(invitation::revoke));
        assertEquals(PropertyManagementError.INVITATION_NOT_PENDING, errorOf(() -> invitation.accept(5L, "carlos.rojas@gmail.com", Instant.now())));
    }

    @Test
    void isRefusedForOccupiedStoresCommonAreasAndInvalidEmails() {
        var occupied = store();
        occupied.assignTenant();
        var area = new CommercialUnit(1L, UnitType.COMMON_AREA, "Pasillo norte", "1", 40.0, null);

        assertEquals(PropertyManagementError.UNIT_NOT_AVAILABLE, errorOf(() -> new TenantInvitation(occupied, "a@b.pe")));
        assertEquals(PropertyManagementError.COMMON_AREA_WITHOUT_TENANT, errorOf(() -> new TenantInvitation(area, "a@b.pe")));
        assertEquals(PropertyManagementError.INVALID_EMAIL, errorOf(() -> new TenantInvitation(store(), "carlos.rojas")));
    }
}
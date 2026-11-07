package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events.TenantInvitedEvent;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.InvitationEmail;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.InvitationStatus;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

/**
 * Invitation link sent to the future tenant of a store (Aggregate Root, US-12). It is valid for
 * seven days; while pending it can be revoked, and a new one can be sent when it expires.
 */
@Entity
@Table(name = "tenant_invitations")
@Getter
public class TenantInvitation extends AuditableAbstractAggregateRoot<TenantInvitation> {

    public static final int VALIDITY_DAYS = 7;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Column(nullable = false)
    private Long unitId;

    @Column(nullable = false)
    private Long galleryId;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "email", nullable = false))
    private InvitationEmail email;

    @Column(nullable = false, unique = true, length = 16)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Column(nullable = false)
    private Instant sentAt;

    @Column(nullable = false)
    private Instant expiresAt;

    protected TenantInvitation() {
    }

    /** Issues the invitation for a store; the unit must be able to receive a tenant. */
    public TenantInvitation(CommercialUnit unit, String email) {
        if (unit == null)
            throw new IllegalArgumentException("Unit is required");
        if (unit.isCommonArea())
            throw new BusinessRuleException(PropertyManagementError.COMMON_AREA_WITHOUT_TENANT);
        if (!unit.canReceiveTenant())
            throw new BusinessRuleException(PropertyManagementError.UNIT_NOT_AVAILABLE);
        this.unitId = unit.getId();
        this.galleryId = unit.getGalleryId();
        this.email = new InvitationEmail(email);
        this.token = newToken();
        this.status = InvitationStatus.PENDING;
        this.sentAt = Instant.now();
        this.expiresAt = sentAt.plus(VALIDITY_DAYS, ChronoUnit.DAYS);
    }

    /** Publishes the event once the id is known, so Property Communication can deliver the link. */
    public void confirmIssued() {
        registerEvent(new TenantInvitedEvent(getId(), galleryId, unitId, email.address(), token, sentAt));
    }

    public String getEmailAddress() {
        return email.address();
    }

    /** Stored status, or EXPIRED when a pending invitation passed its expiry date. */
    public InvitationStatus statusAt(Instant now) {
        if (status == InvitationStatus.PENDING && !now.isBefore(expiresAt))
            return InvitationStatus.EXPIRED;
        return status;
    }

    public boolean isPending() {
        return status == InvitationStatus.PENDING;
    }

    public void revoke() {
        if (!isPending())
            throw new BusinessRuleException(PropertyManagementError.INVITATION_NOT_PENDING);
        this.status = InvitationStatus.REVOKED;
    }

    public void accept(Instant now) {
        if (!isPending())
            throw new BusinessRuleException(PropertyManagementError.INVITATION_NOT_PENDING);
        if (statusAt(now) == InvitationStatus.EXPIRED)
            throw new BusinessRuleException(PropertyManagementError.INVITATION_EXPIRED);
        this.status = InvitationStatus.ACCEPTED;
    }

    private static String newToken() {
        var bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}

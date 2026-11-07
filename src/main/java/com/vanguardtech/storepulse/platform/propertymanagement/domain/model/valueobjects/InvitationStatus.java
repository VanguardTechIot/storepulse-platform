package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects;

/** Lifecycle of a tenant invitation. EXPIRED is derived from the expiry date, never stored. */
public enum InvitationStatus {
    PENDING,
    ACCEPTED,
    REVOKED,
    EXPIRED
}

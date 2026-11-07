package com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects;

/** Controls whether an account can sign in. */
public enum UserStatus {
    /** The account is enabled and may sign in. */
    ACTIVE,
    /** The account is disabled and may not sign in. */
    INACTIVE
}

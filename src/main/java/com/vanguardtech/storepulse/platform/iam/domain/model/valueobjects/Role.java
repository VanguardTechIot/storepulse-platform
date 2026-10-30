package com.vanguardtech.storepulse.platform.iam.domain.model.valueobjects;

/** Determines what an authenticated user can see and do. */
public enum Role {
    /** Manages a commercial gallery from the web application. */
    GALLERY_ADMINISTRATOR,
    /** Rents a unit of a gallery and uses the mobile application. */
    TENANT
}

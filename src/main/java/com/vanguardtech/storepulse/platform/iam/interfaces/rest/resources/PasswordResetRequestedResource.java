package com.vanguardtech.storepulse.platform.iam.interfaces.rest.resources;

import java.time.Instant;

/**
 * Answer of {@code POST /password-recoveries}: when the code stops being accepted, so the web
 * application can show the remaining time.
 *
 * @param expiresAt moment the recovery code expires
 */
public record PasswordResetRequestedResource(Instant expiresAt) {
}

package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

/** {@code status=PENDING} retries a failed notification; {@code read=true} marks it as opened. */
public record UpdateTenantNotificationResource(String status, Boolean read) {
}

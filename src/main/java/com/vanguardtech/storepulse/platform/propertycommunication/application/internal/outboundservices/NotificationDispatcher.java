package com.vanguardtech.storepulse.platform.propertycommunication.application.internal.outboundservices;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantNotification;

/**
 * Delivers a tenant notification through its channel. The infrastructure decides how (in-app,
 * email, push); the domain only learns whether the delivery succeeded.
 */
public interface NotificationDispatcher {
    /** @return {@code true} when the notification was delivered */
    boolean dispatch(TenantNotification notification);
}

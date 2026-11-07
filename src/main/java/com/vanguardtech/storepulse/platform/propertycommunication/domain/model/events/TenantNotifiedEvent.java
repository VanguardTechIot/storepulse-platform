package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;
import java.time.Instant;

/** A notification reached its tenant; Dashboard and Analytics may count it. */
public record TenantNotifiedEvent(Long notificationId, Long tenantId, String type, Instant occurredOn)
        implements DomainEvent {
}

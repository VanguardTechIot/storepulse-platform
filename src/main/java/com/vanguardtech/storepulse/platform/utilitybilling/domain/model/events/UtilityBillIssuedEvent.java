package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityBillId;

import java.time.Instant;

/** The bill was issued: Management-Tenant Communication reacts to it to notify the tenant. */
public record UtilityBillIssuedEvent(UtilityBillId utilityBillId, Long unitId, Instant occurredOn)
        implements DomainEvent {
}
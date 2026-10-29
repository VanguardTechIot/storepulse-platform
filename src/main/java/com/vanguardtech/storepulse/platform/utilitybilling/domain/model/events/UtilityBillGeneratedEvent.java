package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.events;

import com.vanguardtech.storepulse.platform.shared.domain.model.events.DomainEvent;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityBillId;

import java.time.Instant;

public record UtilityBillGeneratedEvent(UtilityBillId utilityBillId, Instant occurredOn) implements DomainEvent {
}
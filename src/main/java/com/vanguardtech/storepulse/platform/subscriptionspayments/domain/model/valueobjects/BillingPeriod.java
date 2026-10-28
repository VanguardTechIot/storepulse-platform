package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import jakarta.persistence.Embeddable;

import java.time.Instant;
import java.time.ZoneOffset;

/** Start and expiration date of a paid subscription period. */
@Embeddable
public record BillingPeriod(Instant startDate, Instant endDate) {

    public BillingPeriod {
        if (!endDate.isAfter(startDate)) throw new BusinessRuleException(SubscriptionsError.INVALID_PERIOD);
    }

    /** Period of one cycle that begins at {@code start}. */
    public static BillingPeriod startingAt(Instant start, BillingCycle cycle) {
        var startUtc = start.atZone(ZoneOffset.UTC);
        var end = cycle == BillingCycle.ANNUAL ? startUtc.plusYears(1) : startUtc.plusMonths(1);
        return new BillingPeriod(start, end.toInstant());
    }
}
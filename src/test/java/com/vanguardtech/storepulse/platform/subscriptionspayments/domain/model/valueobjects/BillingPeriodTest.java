package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BillingPeriodTest {

    private static final Instant START = Instant.parse("2026-01-31T10:00:00Z");

    @Test
    void lastsOneMonthOrOneYearFromItsStart() {
        assertEquals(Instant.parse("2026-02-28T10:00:00Z"),
                BillingPeriod.startingAt(START, BillingCycle.MONTHLY).endDate());
        assertEquals(Instant.parse("2027-01-31T10:00:00Z"),
                BillingPeriod.startingAt(START, BillingCycle.ANNUAL).endDate());
    }

    @Test
    void rejectsAnEndThatIsNotAfterTheStart() {
        assertThrows(BusinessRuleException.class, () -> new BillingPeriod(START, START));
    }
}
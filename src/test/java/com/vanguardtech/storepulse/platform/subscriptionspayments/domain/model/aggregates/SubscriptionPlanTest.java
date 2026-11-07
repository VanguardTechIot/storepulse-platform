package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingCycle;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PlanCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionPlanTest {

    @Test
    void allowsUnitsUpToItsLimitOrAnyWhenUnlimited() {
        var basic = new SubscriptionPlan(PlanCode.BASIC, 30, BigDecimal.valueOf(290), BigDecimal.valueOf(2900), List.of());
        var corporate = new SubscriptionPlan(PlanCode.CORPORATE, null, BigDecimal.valueOf(1490),
                BigDecimal.valueOf(14900), List.of());

        assertTrue(basic.allowsUnits(30));
        assertFalse(basic.allowsUnits(31));
        assertTrue(corporate.allowsUnits(500));
    }

    @Test
    void chargesThePriceOfTheChosenCycle() {
        var basic = new SubscriptionPlan(PlanCode.BASIC, 30, BigDecimal.valueOf(290), BigDecimal.valueOf(2900), List.of());

        assertEquals(BigDecimal.valueOf(290), basic.priceFor(BillingCycle.MONTHLY).amount());
        assertEquals(BigDecimal.valueOf(2900), basic.priceFor(BillingCycle.ANNUAL).amount());
        assertEquals("PEN", basic.priceFor(BillingCycle.ANNUAL).currency());
    }
}
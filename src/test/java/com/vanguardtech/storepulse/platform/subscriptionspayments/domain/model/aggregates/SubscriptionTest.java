package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.AdministratorId;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingCycle;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.GalleryId;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PlanCode;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.SubscriptionStatus;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionTest {

    private static final SubscriptionPlan BASIC =
            new SubscriptionPlan(PlanCode.BASIC, 30, BigDecimal.valueOf(290), BigDecimal.valueOf(2900), List.of());

    private static Subscription subscriptionWith(int units) {
        return new Subscription(new GalleryId(1L), new AdministratorId(1L), BASIC, BillingCycle.MONTHLY, units);
    }

    @Test
    void startsPendingAndRejectsMoreUnitsThanThePlanAllows() {
        assertEquals(SubscriptionStatus.PENDING_PAYMENT, subscriptionWith(10).getStatus());
        var error = assertThrows(BusinessRuleException.class, () -> subscriptionWith(31));
        assertEquals("unit_limit_exceeded", error.getMessage());
    }

    @Test
    void activatesWithANewPeriodAndAutomaticRenewal() {
        var subscription = subscriptionWith(10);

        subscription.activate(BASIC, BillingCycle.ANNUAL, "Visa •••• 4821", Instant.parse("2026-10-30T00:00:00Z"));

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        assertEquals(Instant.parse("2027-10-30T00:00:00Z"), subscription.getBillingPeriod().endDate());
        assertTrue(subscription.isAutoRenewal());
    }

    @Test
    void cancelsAndReactivatesTheRenewalOnlyOnce() {
        var subscription = subscriptionWith(10);
        assertEquals("subscription_not_active",
                assertThrows(BusinessRuleException.class, subscription::cancelRenewal).getMessage());

        subscription.activate(BASIC, BillingCycle.MONTHLY, "Visa •••• 4821", Instant.now());
        subscription.cancelRenewal();
        assertFalse(subscription.isAutoRenewal());
        assertEquals("renewal_already_disabled",
                assertThrows(BusinessRuleException.class, subscription::cancelRenewal).getMessage());

        subscription.reactivateRenewal();
        assertEquals("renewal_already_enabled",
                assertThrows(BusinessRuleException.class, subscription::reactivateRenewal).getMessage());
    }
}
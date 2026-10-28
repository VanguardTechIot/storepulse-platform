package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingCycle;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentMethod;

/** Contracts (or changes to) a plan and pays it in the same step. The expiry is {@code MM/YY}. */
public record RequestSubscriptionCommand(Long galleryId, Long galleryAdministratorId, int monitoredUnitCount,
                                         Long planId, BillingCycle billingCycle, PaymentMethod paymentMethod,
                                         String cardNumber, String cardExpiry) {
}
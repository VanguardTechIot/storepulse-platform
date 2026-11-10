package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources;

import java.time.Instant;

public record SubscriptionResource(Long id, Long galleryId, Long galleryAdministratorId, Long planId, String status,
                                   String billingCycle, Instant startDate, Instant endDate, int monitoredUnitCount,
                                   boolean autoRenewal, String paymentMethodLabel) {
}
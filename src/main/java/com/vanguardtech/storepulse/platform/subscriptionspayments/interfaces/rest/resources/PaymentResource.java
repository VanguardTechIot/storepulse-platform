package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResource(Long id, Long subscriptionId, String receiptNumber, Long planId, String billingCycle,
                              BigDecimal amount, String currency, String method, String methodLabel, String status,
                              String gatewayReference, Instant registeredAt) {
}
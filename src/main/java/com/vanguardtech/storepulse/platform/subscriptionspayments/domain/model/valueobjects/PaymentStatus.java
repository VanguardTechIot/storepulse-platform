package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects;

/** Result of the charge returned by the payment gateway. */
public enum PaymentStatus {
    PENDING, ACCEPTED, REJECTED
}
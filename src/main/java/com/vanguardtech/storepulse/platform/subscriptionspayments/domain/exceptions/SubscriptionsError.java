package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions;

/** Business outcomes that prevent a subscription operation from completing. */
public enum SubscriptionsError {
    PLAN_NOT_FOUND,
    SUBSCRIPTION_NOT_FOUND,
    UNIT_LIMIT_EXCEEDED,
    INVALID_AMOUNT,
    INVALID_PERIOD,
    INVALID_CARD,
    PAYMENT_REJECTED,
    GATEWAY_UNAVAILABLE,
    SUBSCRIPTION_NOT_ACTIVE,
    SUBSCRIPTION_EXPIRED,
    RENEWAL_ALREADY_DISABLED,
    RENEWAL_ALREADY_ENABLED
}
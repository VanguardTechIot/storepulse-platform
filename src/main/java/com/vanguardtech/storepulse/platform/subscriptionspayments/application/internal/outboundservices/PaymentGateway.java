package com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.outboundservices;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.Money;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentCard;

/** External payment gateway that charges the card of the gallery administrator. */
public interface PaymentGateway {

    /** Throws {@code gateway_unavailable} when the gateway does not answer. */
    Charge charge(Money amount, PaymentCard card);

    record Charge(boolean accepted, String reference) {
    }
}
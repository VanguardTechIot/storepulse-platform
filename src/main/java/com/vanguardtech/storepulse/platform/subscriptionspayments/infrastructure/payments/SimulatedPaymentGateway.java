package com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.payments;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.outboundservices.PaymentGateway;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.Money;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentCard;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Stands in for the real payment gateway until one is contracted. Test cards: one ending in
 * {@code 0002} is rejected and one ending in {@code 0119} leaves the gateway unreachable.
 */
@Service
public class SimulatedPaymentGateway implements PaymentGateway {

    @Override
    public Charge charge(Money amount, PaymentCard card) {
        if (card.lastFourDigits().equals("0119")) throw new BusinessRuleException(SubscriptionsError.GATEWAY_UNAVAILABLE);
        return new Charge(!card.lastFourDigits().equals("0002"), "ch_sim_" + UUID.randomUUID());
    }
}
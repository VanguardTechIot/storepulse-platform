package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/** Amount and currency of a price or a charge. The amount is never negative. */
@Embeddable
public record Money(BigDecimal amount, String currency) {

    public Money {
        if (amount == null || amount.signum() < 0) throw new BusinessRuleException(SubscriptionsError.INVALID_AMOUNT);
    }
}
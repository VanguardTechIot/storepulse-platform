package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentCardTest {

    private static final YearMonth OCTOBER_2026 = YearMonth.of(2026, 10);

    @Test
    void keepsOnlyTheBrandAndTheLastFourDigits() {
        var card = PaymentCard.of("4111 1111 1111 4821", "12/27", OCTOBER_2026);

        assertEquals("Visa •••• 4821", card.label());
    }

    @Test
    void rejectsMalformedNumbersAndExpiredOrInvalidDates() {
        var error = assertThrows(BusinessRuleException.class, () -> PaymentCard.of("4111", "12/27", OCTOBER_2026));
        assertEquals("invalid_card", error.getMessage());
        assertThrows(BusinessRuleException.class, () -> PaymentCard.of("4111111111114821", "09/26", OCTOBER_2026));
        assertThrows(BusinessRuleException.class, () -> PaymentCard.of("4111111111114821", "13/27", OCTOBER_2026));
    }
}
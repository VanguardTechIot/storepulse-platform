package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Card typed in the checkout. Only the brand and the last four digits are kept: the payment
 * gateway processes the full number and StorePulse never stores it.
 */
public record PaymentCard(String brand, String lastFourDigits) {

    private static final DateTimeFormatter EXPIRY = DateTimeFormatter.ofPattern("MM/yy");

    /** Validates the number (13 to 19 digits) and the {@code MM/YY} expiry against the current month. */
    public static PaymentCard of(String number, String expiry, YearMonth currentMonth) {
        var digits = number.replaceAll("\\s+", "");
        if (!digits.matches("\\d{13,19}") || expiryOf(expiry).isBefore(currentMonth)) {
            throw new BusinessRuleException(SubscriptionsError.INVALID_CARD);
        }
        return new PaymentCard(brandOf(digits), digits.substring(digits.length() - 4));
    }

    /** Text shown in the subscription and the payment history, for example {@code Visa •••• 4821}. */
    public String label() {
        return brand + " •••• " + lastFourDigits;
    }

    private static YearMonth expiryOf(String expiry) {
        try {
            return YearMonth.parse(expiry.trim(), EXPIRY);
        } catch (DateTimeParseException exception) {
            throw new BusinessRuleException(SubscriptionsError.INVALID_CARD);
        }
    }

    private static String brandOf(String digits) {
        if (digits.startsWith("4")) return "Visa";
        if (digits.matches("5[1-5]\\d+")) return "Mastercard";
        if (digits.matches("3[47]\\d+")) return "Amex";
        return "Card";
    }
}
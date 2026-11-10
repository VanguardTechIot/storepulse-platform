package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.time.YearMonth;

/** First and last day (inclusive) of the period a bill covers. */
@Embeddable
public record BillingPeriod(LocalDate startDate, LocalDate endDate) {

    public BillingPeriod {
        if (startDate == null || endDate == null || endDate.isBefore(startDate))
            throw new BusinessRuleException(UtilityBillingError.INVALID_PERIOD);
    }

    /** The whole calendar month, as the web application builds it from a "yyyy-MM" value. */
    public static BillingPeriod ofMonth(YearMonth month) {
        return new BillingPeriod(month.atDay(1), month.atEndOfMonth());
    }
}
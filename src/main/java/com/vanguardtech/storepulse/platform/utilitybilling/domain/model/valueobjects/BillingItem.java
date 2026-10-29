package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Line of a bill: the consumption of one utility and what it costs. Immutable once created. */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillingItem {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UtilityType utilityType;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal consumption;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal baseline;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal rate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    public BillingItem(UtilityType utilityType, BigDecimal consumption, BigDecimal baseline, BigDecimal rate) {
        if (utilityType == null || consumption == null || baseline == null || rate == null
                || consumption.signum() < 0 || baseline.signum() < 0 || rate.signum() < 0)
            throw new BusinessRuleException(UtilityBillingError.INVALID_AMOUNT);
        this.utilityType = utilityType;
        this.consumption = consumption;
        this.baseline = baseline;
        this.rate = rate;
        this.amount = consumption.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public String getUnitOfMeasure() {
        return utilityType.getUnitOfMeasure();
    }
}
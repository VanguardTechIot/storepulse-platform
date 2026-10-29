package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.entities;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

/**
 * Line of the breakdown of a bill: the consumption of one utility and what it costs. It is only
 * created through its bill and never changes afterwards.
 */
@Entity
@Table(name = "billing_items")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BillingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UtilityType utilityType;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal consumptionValue;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal baseline;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal rate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    protected BillingItem() {
    }

    public BillingItem(UtilityType utilityType, BigDecimal consumptionValue, BigDecimal baseline, BigDecimal rate) {
        if (utilityType == null || consumptionValue == null || baseline == null || rate == null
                || consumptionValue.signum() < 0 || baseline.signum() < 0 || rate.signum() < 0)
            throw new BusinessRuleException(UtilityBillingError.INVALID_AMOUNT);
        this.utilityType = utilityType;
        this.consumptionValue = consumptionValue;
        this.unit = utilityType.getUnitOfMeasure();
        this.baseline = baseline;
        this.rate = rate;
        this.amount = calculateAmount();
    }

    /** Consumption times rate, rounded to cents. */
    public BigDecimal calculateAmount() {
        return consumptionValue.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
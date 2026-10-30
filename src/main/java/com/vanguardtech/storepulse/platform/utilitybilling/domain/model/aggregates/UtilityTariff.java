package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Price per unit of consumption of a utility, applied to the periods that start on or after its
 * effective date. The newest tariff in force for a period is the one the bill uses.
 */
@Entity
@Table(name = "utility_tariffs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UtilityTariff extends AuditableAbstractAggregateRoot<UtilityTariff> {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UtilityType utilityType;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal rate;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    public UtilityTariff(UtilityType utilityType, BigDecimal rate, LocalDate effectiveFrom) {
        if (utilityType == null || rate == null || rate.signum() <= 0 || effectiveFrom == null)
            throw new BusinessRuleException(UtilityBillingError.INVALID_AMOUNT);
        this.utilityType = utilityType;
        this.rate = rate;
        this.effectiveFrom = effectiveFrom;
    }
}
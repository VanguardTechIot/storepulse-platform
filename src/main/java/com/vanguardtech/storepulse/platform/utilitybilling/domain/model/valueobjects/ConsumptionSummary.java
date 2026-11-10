package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;

import java.math.BigDecimal;

/** Consumption of one utility received from another context to support a bill. */
public record ConsumptionSummary(UtilityType utilityType, BigDecimal consumption) {

    public ConsumptionSummary {
        if (utilityType == null || consumption == null || consumption.signum() < 0)
            throw new BusinessRuleException(UtilityBillingError.INVALID_AMOUNT);
    }
}
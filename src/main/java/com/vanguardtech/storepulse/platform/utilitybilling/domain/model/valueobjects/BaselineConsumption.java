package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;

import java.math.BigDecimal;

/** Reference consumption of a utility against which a bill's consumption is reviewed. */
public record BaselineConsumption(UtilityType utilityType, BigDecimal value) {

    public BaselineConsumption {
        if (utilityType == null || value == null || value.signum() < 0)
            throw new BusinessRuleException(UtilityBillingError.INVALID_AMOUNT);
    }
}
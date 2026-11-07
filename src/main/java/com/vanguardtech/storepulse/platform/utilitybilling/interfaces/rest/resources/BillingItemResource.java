package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources;

import java.math.BigDecimal;

public record BillingItemResource(String utilityType, BigDecimal consumption, BigDecimal baseline,
                                  String unitOfMeasure, BigDecimal rate, BigDecimal amount) {
}
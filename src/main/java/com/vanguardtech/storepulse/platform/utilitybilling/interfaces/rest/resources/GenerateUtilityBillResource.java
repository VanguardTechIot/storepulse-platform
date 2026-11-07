package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.YearMonth;

/** The period travels as {@code yyyy-MM}. The unit is the resource Monitoring records consumption against. */
public record GenerateUtilityBillResource(@NotNull @Positive Long unitId, @NotBlank String unitCode, String unitName,
                                          String tenantName, @NotNull YearMonth period) {
}
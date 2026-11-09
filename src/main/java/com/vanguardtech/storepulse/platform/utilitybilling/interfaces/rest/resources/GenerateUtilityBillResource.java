package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.YearMonth;

/** {@code unitId} is the store of Property Management; the period travels as {@code yyyy-MM}. */
public record GenerateUtilityBillResource(@NotNull @Positive Long unitId, @NotNull YearMonth period) {
}
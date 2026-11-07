package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** {@code type} is STORE or COMMON_AREA; {@code businessName} applies to stores only. */
public record RegisterCommercialUnitResource(@NotBlank String type, @NotBlank String code, @NotBlank String floor,
                                             @NotNull @Positive Double areaSquareMeters, String businessName) {
}

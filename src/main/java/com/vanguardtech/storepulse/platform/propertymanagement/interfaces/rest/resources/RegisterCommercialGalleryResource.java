package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** The administrator is the authenticated user; the body does not carry it. */
public record RegisterCommercialGalleryResource(@NotBlank String name, @NotBlank String address,
                                                @NotNull @Positive Integer totalUnits) {
}

package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCommercialGalleryResource(@NotBlank String name, @NotBlank String address,
                                              @NotNull @Positive Integer totalUnits) {
}

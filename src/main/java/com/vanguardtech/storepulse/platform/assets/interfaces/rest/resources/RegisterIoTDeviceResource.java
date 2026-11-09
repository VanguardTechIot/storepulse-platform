package com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** {@code resourceId} must be linked to a unit of the administrator's gallery. */
public record RegisterIoTDeviceResource(@NotBlank String serialNumber, @NotBlank String type,
                                        @NotNull @Positive Long resourceId) {
}
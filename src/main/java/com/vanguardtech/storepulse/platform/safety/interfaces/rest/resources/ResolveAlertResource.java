package com.vanguardtech.storepulse.platform.safety.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResolveAlertResource(@NotBlank @Size(max = 255) String resolutionNote) {
}

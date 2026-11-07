package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** {@code type} is ANNOUNCEMENT or INCIDENT; {@code unitIds} is ignored when {@code wholeGallery} is true. */
public record PublishAnnouncementResource(@NotBlank String type, List<Long> unitIds, boolean wholeGallery,
                                          @NotBlank String title, @NotBlank String body, String channel) {
}

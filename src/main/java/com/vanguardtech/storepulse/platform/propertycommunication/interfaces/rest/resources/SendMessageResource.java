package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** The sender and their role come from the token. */
public record SendMessageResource(@NotBlank String content, List<AttachmentResource> attachments) {
}
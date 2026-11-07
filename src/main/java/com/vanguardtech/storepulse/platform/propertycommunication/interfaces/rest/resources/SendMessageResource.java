package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** {@code senderRole} is TENANT or GALLERY_ADMINISTRATOR; the sender defaults to the authenticated user. */
public record SendMessageResource(Long senderId, @NotBlank String senderRole, @NotBlank String content,
                                  List<AttachmentResource> attachments) {
}

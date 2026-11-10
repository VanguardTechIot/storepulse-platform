package com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.TenantInvitation;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.rest.resources.TenantInvitationResource;
import java.time.Instant;

public class TenantInvitationResourceFromEntityAssembler {
    public static TenantInvitationResource toResourceFromEntity(TenantInvitation invitation) {
        return new TenantInvitationResource(invitation.getId(), invitation.getUnitId(), invitation.getGalleryId(),
                invitation.getEmailAddress(), invitation.getToken(), invitation.statusAt(Instant.now()).name(),
                invitation.getSentAt(), invitation.getExpiresAt());
    }
}

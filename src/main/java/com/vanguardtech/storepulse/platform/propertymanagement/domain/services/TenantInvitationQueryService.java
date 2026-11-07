package com.vanguardtech.storepulse.platform.propertymanagement.domain.services;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.TenantInvitation;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationByIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationByTokenQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationsByGalleryIdQuery;
import java.util.List;
import java.util.Optional;

public interface TenantInvitationQueryService {
    List<TenantInvitation> handle(GetTenantInvitationsByGalleryIdQuery query);
    Optional<TenantInvitation> handle(GetTenantInvitationByIdQuery query);
    Optional<TenantInvitation> handle(GetTenantInvitationByTokenQuery query);
}

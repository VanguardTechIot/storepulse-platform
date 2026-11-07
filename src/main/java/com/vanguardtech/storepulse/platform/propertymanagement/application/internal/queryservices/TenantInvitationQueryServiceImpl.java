package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.TenantInvitation;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationByIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationByTokenQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetTenantInvitationsByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.TenantInvitationQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.TenantInvitationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TenantInvitationQueryServiceImpl implements TenantInvitationQueryService {

    private final TenantInvitationRepository invitationRepository;

    public TenantInvitationQueryServiceImpl(TenantInvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @Override
    public List<TenantInvitation> handle(GetTenantInvitationsByGalleryIdQuery query) {
        return invitationRepository.findByGalleryIdOrderBySentAtDesc(query.galleryId());
    }

    @Override
    public Optional<TenantInvitation> handle(GetTenantInvitationByIdQuery query) {
        return invitationRepository.findById(query.invitationId());
    }

    @Override
    public Optional<TenantInvitation> handle(GetTenantInvitationByTokenQuery query) {
        return invitationRepository.findByToken(query.token());
    }
}

package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.CommercialGalleryRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CommercialGalleryQueryServiceImpl implements CommercialGalleryQueryService {

    private final CommercialGalleryRepository galleryRepository;

    public CommercialGalleryQueryServiceImpl(CommercialGalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    @Override
    public Optional<CommercialGallery> handle(GetCommercialGalleryByIdQuery query) {
        return galleryRepository.findById(query.galleryId());
    }

    @Override
    public Optional<CommercialGallery> handle(GetCommercialGalleryByAdministratorIdQuery query) {
        return galleryRepository.findByAdministratorId(query.administratorId());
    }
}

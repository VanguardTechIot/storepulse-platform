package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialUnitByIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialUnitsByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.CommercialUnitRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommercialUnitQueryServiceImpl implements CommercialUnitQueryService {

    private final CommercialUnitRepository unitRepository;

    public CommercialUnitQueryServiceImpl(CommercialUnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<CommercialUnit> handle(GetCommercialUnitsByGalleryIdQuery query) {
        return unitRepository.findByGalleryIdOrderByFloorAscCodeAsc(query.galleryId());
    }

    @Override
    public Optional<CommercialUnit> handle(GetCommercialUnitByIdQuery query) {
        return unitRepository.findById(query.unitId());
    }
}

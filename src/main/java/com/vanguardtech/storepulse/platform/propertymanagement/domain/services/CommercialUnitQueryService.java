package com.vanguardtech.storepulse.platform.propertymanagement.domain.services;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialUnitByIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialUnitsByGalleryIdQuery;
import java.util.List;
import java.util.Optional;

public interface CommercialUnitQueryService {
    List<CommercialUnit> handle(GetCommercialUnitsByGalleryIdQuery query);
    Optional<CommercialUnit> handle(GetCommercialUnitByIdQuery query);
}

package com.vanguardtech.storepulse.platform.propertymanagement.domain.services;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByIdQuery;
import java.util.Optional;

public interface CommercialGalleryQueryService {
    Optional<CommercialGallery> handle(GetCommercialGalleryByIdQuery query);
    Optional<CommercialGallery> handle(GetCommercialGalleryByAdministratorIdQuery query);
}

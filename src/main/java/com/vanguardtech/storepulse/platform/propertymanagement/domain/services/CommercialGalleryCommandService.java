package com.vanguardtech.storepulse.platform.propertymanagement.domain.services;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RegisterCommercialGalleryCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.UpdateCommercialGalleryInfoCommand;

public interface CommercialGalleryCommandService {
    CommercialGallery handle(RegisterCommercialGalleryCommand command);
    CommercialGallery handle(UpdateCommercialGalleryInfoCommand command);
}

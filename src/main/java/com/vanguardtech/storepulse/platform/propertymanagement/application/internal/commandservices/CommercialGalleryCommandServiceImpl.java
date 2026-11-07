package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RegisterCommercialGalleryCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.UpdateCommercialGalleryInfoCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.CommercialGalleryRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommercialGalleryCommandServiceImpl implements CommercialGalleryCommandService {

    private final CommercialGalleryRepository galleryRepository;

    public CommercialGalleryCommandServiceImpl(CommercialGalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    @Override
    @Transactional
    public CommercialGallery handle(RegisterCommercialGalleryCommand command) {
        if (galleryRepository.existsByAdministratorId(command.administratorId()))
            throw new BusinessRuleException(PropertyManagementError.GALLERY_ALREADY_REGISTERED);
        var gallery = new CommercialGallery(command.administratorId(), command.name(), command.address(),
                command.totalUnits());
        return galleryRepository.save(gallery);
    }

    @Override
    @Transactional
    public CommercialGallery handle(UpdateCommercialGalleryInfoCommand command) {
        var gallery = galleryRepository.findById(command.galleryId())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.GALLERY_NOT_FOUND));
        gallery.updateInfo(command.name(), command.address(), command.totalUnits());
        return galleryRepository.save(gallery);
    }
}

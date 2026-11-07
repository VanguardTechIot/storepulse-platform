package com.vanguardtech.storepulse.platform.propertymanagement.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.ChangeUnitOccupancyCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RegisterCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.RemoveCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.UpdateCommercialUnitCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.CommercialGalleryRepository;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.CommercialUnitRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommercialUnitCommandServiceImpl implements CommercialUnitCommandService {

    private final CommercialUnitRepository unitRepository;
    private final CommercialGalleryRepository galleryRepository;

    public CommercialUnitCommandServiceImpl(CommercialUnitRepository unitRepository,
                                            CommercialGalleryRepository galleryRepository) {
        this.unitRepository = unitRepository;
        this.galleryRepository = galleryRepository;
    }

    @Override
    @Transactional
    public CommercialUnit handle(RegisterCommercialUnitCommand command) {
        if (!galleryRepository.existsById(command.galleryId()))
            throw new ResourceNotFoundException(PropertyManagementError.GALLERY_NOT_FOUND);
        var unit = new CommercialUnit(command.galleryId(), command.type(), command.code(), command.floor(),
                command.areaSquareMeters(), command.businessName());
        ensureUniqueCode(unit, null);
        return unitRepository.save(unit);
    }

    @Override
    @Transactional
    public CommercialUnit handle(UpdateCommercialUnitCommand command) {
        var unit = findUnit(command.unitId());
        unit.update(command.code(), command.floor(), command.areaSquareMeters(), command.businessName());
        ensureUniqueCode(unit, unit.getId());
        if (command.status() != null && command.status() != unit.getStatus())
            unit.changeStatus(command.status());
        return unitRepository.save(unit);
    }

    @Override
    @Transactional
    public CommercialUnit handle(RemoveCommercialUnitCommand command) {
        var unit = findUnit(command.unitId());
        unit.remove();
        return unitRepository.save(unit);
    }

    @Override
    @Transactional
    public CommercialUnit handle(ChangeUnitOccupancyCommand command) {
        var unit = findUnit(command.unitId());
        if (command.occupied())
            unit.assignTenant();
        else
            unit.vacate();
        return unitRepository.save(unit);
    }

    private CommercialUnit findUnit(Long unitId) {
        return unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.UNIT_NOT_FOUND));
    }

    /** US-09, US-10, US-57: the code of a store or the name of a common area is unique in the gallery. */
    private void ensureUniqueCode(CommercialUnit unit, Long ownId) {
        var taken = unitRepository.findByGalleryIdAndActiveTrue(unit.getGalleryId()).stream()
                .anyMatch(other -> !other.getId().equals(ownId) && other.hasCode(unit.getCode()));
        if (taken)
            throw new BusinessRuleException(unit.isStore()
                    ? PropertyManagementError.DUPLICATED_UNIT_CODE
                    : PropertyManagementError.DUPLICATED_COMMON_AREA_NAME);
    }
}

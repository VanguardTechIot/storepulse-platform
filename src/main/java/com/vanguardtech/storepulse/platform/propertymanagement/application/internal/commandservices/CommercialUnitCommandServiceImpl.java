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
        if (!galleryRepository.existsByIdAndAdministratorId(command.galleryId(), command.administratorId()))
            throw new ResourceNotFoundException(PropertyManagementError.GALLERY_NOT_FOUND);
        var unit = new CommercialUnit(command.galleryId(), command.type(), command.code(), command.floor(),
                command.areaSquareMeters(), command.businessName());
        unit.linkResource(command.resourceId());
        ensureUniqueCode(unit, null);
        return unitRepository.save(unit);
    }

    @Override
    @Transactional
    public CommercialUnit handle(UpdateCommercialUnitCommand command) {
        var unit = findUnitOfAdministrator(command.unitId(), command.administratorId());
        unit.update(command.code(), command.floor(), command.areaSquareMeters(), command.businessName());
        unit.linkResource(command.resourceId());
        ensureUniqueCode(unit, unit.getId());
        if (command.status() != null && command.status() != unit.getStatus())
            unit.changeStatus(command.status());
        return unitRepository.save(unit);
    }

    @Override
    @Transactional
    public CommercialUnit handle(RemoveCommercialUnitCommand command) {
        var unit = findUnitOfAdministrator(command.unitId(), command.administratorId());
        unit.remove();
        return unitRepository.save(unit);
    }

    @Override
    @Transactional
    public CommercialUnit handle(ChangeUnitOccupancyCommand command) {
        var unit = unitRepository.findById(command.unitId())
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.UNIT_NOT_FOUND));
        if (command.occupied())
            unit.assignTenant();
        else
            unit.vacate();
        return unitRepository.save(unit);
    }

    /** A unit of another administrator's gallery is answered as not found. */
    private CommercialUnit findUnitOfAdministrator(Long unitId, Long administratorId) {
        return unitRepository.findByIdAndAdministratorId(unitId, administratorId)
                .orElseThrow(() -> new ResourceNotFoundException(PropertyManagementError.UNIT_NOT_FOUND));
    }

    /** The code of a store or the name of a common area is unique among the active units of the gallery. */
    private void ensureUniqueCode(CommercialUnit unit, Long ownId) {
        var taken = unitRepository.findByGalleryIdAndActiveTrue(unit.getGalleryId()).stream()
                .anyMatch(other -> !other.getId().equals(ownId) && other.hasCode(unit.getCode()));
        if (taken)
            throw new BusinessRuleException(unit.isStore()
                    ? PropertyManagementError.DUPLICATED_UNIT_CODE
                    : PropertyManagementError.DUPLICATED_COMMON_AREA_NAME);
    }
}
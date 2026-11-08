package com.vanguardtech.storepulse.platform.propertymanagement.application.acl;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialGallery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates.CommercialUnit;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.commands.ChangeUnitOccupancyCommand;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByAdministratorIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialGalleryByIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialUnitByIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.queries.GetCommercialUnitsByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialGalleryQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitCommandService;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.services.CommercialUnitQueryService;
import com.vanguardtech.storepulse.platform.propertymanagement.infrastructure.persistence.jpa.repositories.CommercialUnitRepository;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.CommercialUnitSummary;
import com.vanguardtech.storepulse.platform.propertymanagement.interfaces.acl.PropertyManagementContextFacade;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Implementation of {@link PropertyManagementContextFacade} over the services of the context. */
@Service
public class PropertyManagementContextFacadeImpl implements PropertyManagementContextFacade {

    private final CommercialGalleryQueryService galleryQueryService;
    private final CommercialUnitQueryService unitQueryService;
    private final CommercialUnitCommandService unitCommandService;
    private final CommercialUnitRepository unitRepository;

    public PropertyManagementContextFacadeImpl(CommercialGalleryQueryService galleryQueryService,
                                               CommercialUnitQueryService unitQueryService,
                                               CommercialUnitCommandService unitCommandService,
                                               CommercialUnitRepository unitRepository) {
        this.galleryQueryService = galleryQueryService;
        this.unitQueryService = unitQueryService;
        this.unitCommandService = unitCommandService;
        this.unitRepository = unitRepository;
    }

    @Override
    public Optional<Long> fetchGalleryIdByAdministratorId(Long administratorId) {
        return galleryQueryService.handle(new GetCommercialGalleryByAdministratorIdQuery(administratorId))
                .map(CommercialGallery::getId);
    }

    @Override
    public Optional<Long> fetchAdministratorIdByGalleryId(Long galleryId) {
        return galleryQueryService.handle(new GetCommercialGalleryByIdQuery(galleryId))
                .map(CommercialGallery::getAdministratorId);
    }

    @Override
    public Optional<CommercialUnitSummary> fetchUnitById(Long unitId) {
        return unitQueryService.handle(new GetCommercialUnitByIdQuery(unitId)).map(this::summarize);
    }

    @Override
    public List<CommercialUnitSummary> fetchActiveUnitsByGalleryId(Long galleryId) {
        return unitQueryService.handle(new GetCommercialUnitsByGalleryIdQuery(galleryId)).stream()
                .filter(CommercialUnit::isActive)
                .map(this::summarize)
                .toList();
    }

    @Override
    public Optional<Long> fetchResourceIdByUnitId(Long unitId) {
        return unitQueryService.handle(new GetCommercialUnitByIdQuery(unitId)).map(CommercialUnit::getResourceId);
    }

    @Override
    public List<Long> fetchResourceIdsByGalleryId(Long galleryId) {
        return unitRepository.findByGalleryIdAndActiveTrue(galleryId).stream()
                .map(CommercialUnit::getResourceId)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void changeUnitOccupancy(Long unitId, boolean occupied) {
        unitCommandService.handle(new ChangeUnitOccupancyCommand(unitId, occupied));
    }

    private CommercialUnitSummary summarize(CommercialUnit unit) {
        return new CommercialUnitSummary(unit.getId(), unit.getGalleryId(), unit.getCode(), unit.getFloor(),
                unit.getBusinessName(), unit.isStore(), unit.isAvailable(), unit.isActive());
    }
}
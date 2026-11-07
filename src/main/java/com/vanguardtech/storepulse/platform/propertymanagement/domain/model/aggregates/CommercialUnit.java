package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.events.CommercialUnitRemovedEvent;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitDimensions;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitStatus;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitType;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;

/**
 * Store or common area of a gallery (Aggregate Root). Its code is unique within the gallery among
 * active units; a store is removed only when nobody occupies it, and it is the only kind of unit
 * that can receive a tenant.
 */
@Entity
@Table(name = "commercial_units")
@Getter
public class CommercialUnit extends AuditableAbstractAggregateRoot<CommercialUnit> {

    public static final int STORE_CODE_MAX_LENGTH = 10;
    public static final int COMMON_AREA_NAME_MAX_LENGTH = 60;
    public static final int FLOOR_MAX_LENGTH = 10;
    public static final int BUSINESS_NAME_MAX_LENGTH = 80;

    @Column(nullable = false)
    private Long galleryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType type;

    /** Number of the store (A-03) or name of the common area (Patio de comidas). */
    @Column(nullable = false, length = COMMON_AREA_NAME_MAX_LENGTH)
    private String code;

    @Column(nullable = false, length = FLOOR_MAX_LENGTH)
    private String floor;

    @Embedded
    private UnitDimensions dimensions;

    @Column(length = BUSINESS_NAME_MAX_LENGTH)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitStatus status;

    /** False once the unit is logically removed (US-11). */
    @Column(nullable = false)
    private boolean active;

    protected CommercialUnit() {
    }

    public CommercialUnit(Long galleryId, UnitType type, String code, String floor, Double areaSquareMeters,
                          String businessName) {
        if (galleryId == null || galleryId <= 0)
            throw new IllegalArgumentException("Gallery id must be a positive number");
        if (type == null)
            throw new IllegalArgumentException("Unit type is required");
        this.galleryId = galleryId;
        this.type = type;
        this.status = UnitStatus.AVAILABLE;
        this.active = true;
        applyData(code, floor, areaSquareMeters, businessName);
    }

    public boolean isStore() {
        return type == UnitType.STORE;
    }

    public boolean isCommonArea() {
        return type == UnitType.COMMON_AREA;
    }

    public boolean isAvailable() {
        return status == UnitStatus.AVAILABLE;
    }

    public boolean isOccupied() {
        return status == UnitStatus.OCCUPIED;
    }

    public Double getAreaSquareMeters() {
        return dimensions.areaSquareMeters();
    }

    /** Only an active, available store can be assigned or invited. */
    public boolean canReceiveTenant() {
        return active && isStore() && isAvailable();
    }

    /** Tells whether {@code candidate} is the same code as this unit's, ignoring case and spaces. */
    public boolean hasCode(String candidate) {
        return candidate != null && code.trim().equalsIgnoreCase(candidate.trim());
    }

    /** US-10: edits the data; a {@code null} keeps the current value. */
    public void update(String code, String floor, Double areaSquareMeters, String businessName) {
        applyData(code == null ? this.code : code, floor == null ? this.floor : floor,
                areaSquareMeters == null ? getAreaSquareMeters() : areaSquareMeters,
                businessName == null ? this.businessName : businessName);
    }

    public void assignTenant() {
        if (isCommonArea())
            throw new BusinessRuleException(PropertyManagementError.COMMON_AREA_WITHOUT_TENANT);
        if (!canReceiveTenant())
            throw new BusinessRuleException(PropertyManagementError.UNIT_NOT_AVAILABLE);
        this.status = UnitStatus.OCCUPIED;
    }

    public void vacate() {
        if (isOccupied())
            this.status = UnitStatus.AVAILABLE;
    }

    public void markUnderMaintenance() {
        if (isOccupied())
            throw new BusinessRuleException(PropertyManagementError.UNIT_OCCUPIED);
        this.status = UnitStatus.MAINTENANCE;
    }

    public void endMaintenance() {
        if (status == UnitStatus.MAINTENANCE)
            this.status = UnitStatus.AVAILABLE;
    }

    /** Applies the status asked by the administrator from the unit detail. */
    public void changeStatus(UnitStatus requested) {
        switch (requested) {
            case OCCUPIED -> assignTenant();
            case MAINTENANCE -> markUnderMaintenance();
            case AVAILABLE -> {
                vacate();
                endMaintenance();
            }
        }
    }

    /** US-11: logical removal; the unit keeps its history but no longer takes part in the gallery. */
    public void remove() {
        if (isOccupied())
            throw new BusinessRuleException(PropertyManagementError.UNIT_OCCUPIED);
        this.active = false;
        registerEvent(new CommercialUnitRemovedEvent(getId(), galleryId, Instant.now()));
    }

    private void applyData(String code, String floor, Double areaSquareMeters, String businessName) {
        var maxCode = isStore() ? STORE_CODE_MAX_LENGTH : COMMON_AREA_NAME_MAX_LENGTH;
        if (code == null || code.isBlank() || code.trim().length() > maxCode)
            throw new BusinessRuleException(PropertyManagementError.INVALID_UNIT_CODE);
        if (floor == null || floor.isBlank() || floor.trim().length() > FLOOR_MAX_LENGTH)
            throw new BusinessRuleException(PropertyManagementError.INVALID_FLOOR);
        var name = businessName == null || businessName.isBlank() ? null : businessName.trim();
        if (name != null && name.length() > BUSINESS_NAME_MAX_LENGTH)
            throw new BusinessRuleException(PropertyManagementError.INVALID_BUSINESS_NAME);
        this.code = isStore() ? code.trim().toUpperCase() : code.trim();
        this.floor = floor.trim();
        this.dimensions = new UnitDimensions(areaSquareMeters);
        this.businessName = isStore() ? name : null;
    }
}

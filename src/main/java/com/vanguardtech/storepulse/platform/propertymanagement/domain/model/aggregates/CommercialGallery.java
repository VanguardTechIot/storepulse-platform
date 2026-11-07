package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import java.time.Instant;

/**
 * Commercial gallery managed by one administrator (Aggregate Root, US-08). StorePulse manages one
 * gallery per administrator account; its stores and common areas are {@link CommercialUnit}s.
 */
@Entity
@Table(name = "commercial_galleries")
@Getter
public class CommercialGallery extends AuditableAbstractAggregateRoot<CommercialGallery> {

    public static final int NAME_MAX_LENGTH = 80;
    public static final int ADDRESS_MAX_LENGTH = 160;
    public static final int MAX_TOTAL_UNITS = 1000;

    /** Account of Identity and Access Management; there is no foreign key across contexts. */
    @Column(nullable = false, unique = true)
    private Long administratorId;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Column(nullable = false, length = ADDRESS_MAX_LENGTH)
    private String address;

    /** Units the administrator plans to register; the limit shown in the unit list. */
    @Column(nullable = false)
    private Integer totalUnits;

    @Column(nullable = false)
    private Instant registeredAt;

    protected CommercialGallery() {
    }

    public CommercialGallery(Long administratorId, String name, String address, Integer totalUnits) {
        if (administratorId == null || administratorId <= 0)
            throw new IllegalArgumentException("Administrator id must be a positive number");
        this.administratorId = administratorId;
        this.registeredAt = Instant.now();
        updateInfo(name, address, totalUnits);
    }

    /** Replaces name, address and total of units after validating them. */
    public void updateInfo(String name, String address, Integer totalUnits) {
        if (name == null || name.isBlank() || name.trim().length() > NAME_MAX_LENGTH)
            throw new BusinessRuleException(PropertyManagementError.INVALID_GALLERY_NAME);
        if (address == null || address.isBlank() || address.trim().length() > ADDRESS_MAX_LENGTH)
            throw new BusinessRuleException(PropertyManagementError.INVALID_ADDRESS);
        if (totalUnits == null || totalUnits <= 0 || totalUnits > MAX_TOTAL_UNITS)
            throw new BusinessRuleException(PropertyManagementError.INVALID_TOTAL_UNITS);
        this.name = name.trim();
        this.address = address.trim();
        this.totalUnits = totalUnits;
    }
}

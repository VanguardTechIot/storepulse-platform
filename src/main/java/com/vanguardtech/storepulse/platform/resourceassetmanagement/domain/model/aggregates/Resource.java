package com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.valueobjects.AssetStatus;
import com.vanguardtech.storepulse.platform.resourceassetmanagement.domain.model.valueobjects.Location;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "resources")
@Getter
public class Resource extends AuditableAbstractAggregateRoot<Resource> {

    @Column(nullable = false)
    private String name;
    private String description;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    protected Resource(){
    }

    public Resource(String name, String description, Location location){
        if(name == null || name.isBlank())
            throw new IllegalArgumentException("Resource name cannot be null or blank");
        if (location == null)
            throw new IllegalArgumentException("Resource location cannot be null");
        this.name = name;
        this.description = description;
        this.location = location;
        this.status = AssetStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == AssetStatus.ACTIVE;
    }
}

package com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.exceptions.PropertyCommunicationError;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.events.TenantAssignedEvent;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.events.TenantAssignmentTerminatedEvent;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.valueobjects.AssignmentStatus;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;

/**
 * Assignment of a registered tenant to a store (Aggregate Root). A store never has more than one
 * active assignment; the uniqueness is checked by the command service against the repository.
 */
@Entity
@Table(name = "tenant_assignments")
@Getter
public class TenantAssignment extends AuditableAbstractAggregateRoot<TenantAssignment> {

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long commercialUnitId;

    @Column(nullable = false)
    private Long galleryAdministratorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Column(nullable = false)
    private Instant assignedAt;

    private Instant terminatedAt;

    protected TenantAssignment() {
    }

    public TenantAssignment(Long tenantId, Long commercialUnitId, Long galleryAdministratorId) {
        if (tenantId == null || tenantId <= 0)
            throw new IllegalArgumentException("Tenant id must be a positive number");
        if (commercialUnitId == null || commercialUnitId <= 0)
            throw new IllegalArgumentException("Commercial unit id must be a positive number");
        if (galleryAdministratorId == null || galleryAdministratorId <= 0)
            throw new IllegalArgumentException("Gallery administrator id must be a positive number");
        this.tenantId = tenantId;
        this.commercialUnitId = commercialUnitId;
        this.galleryAdministratorId = galleryAdministratorId;
        this.status = AssignmentStatus.ACTIVE;
        this.assignedAt = Instant.now();
    }

    /** Publishes the event once the id is known. */
    public void confirmAssigned() {
        registerEvent(new TenantAssignedEvent(getId(), tenantId, commercialUnitId, assignedAt));
    }

    public boolean isActive() {
        return status == AssignmentStatus.ACTIVE;
    }

    /** TerminateTenantAssignmentCommand: the store becomes free again. */
    public void terminate() {
        if (!isActive())
            throw new BusinessRuleException(PropertyCommunicationError.ASSIGNMENT_NOT_ACTIVE);
        this.status = AssignmentStatus.TERMINATED;
        this.terminatedAt = Instant.now();
        registerEvent(new TenantAssignmentTerminatedEvent(getId(), tenantId, commercialUnitId, terminatedAt));
    }
}

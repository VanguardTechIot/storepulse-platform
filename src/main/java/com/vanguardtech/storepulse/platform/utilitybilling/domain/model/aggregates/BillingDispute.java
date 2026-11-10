package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.events.BillingDisputeRaisedEvent;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.events.BillingDisputeResolvedEvent;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BillingDisputeId;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BillingDisputeStatus;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.DisputeReason;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityBillId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Tenant's dispute over an issued bill. The administrator reviews it and then resolves it. It
 * references the bill and the tenant by identifier only.
 */
@Entity
@Table(name = "billing_disputes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillingDispute extends AuditableAbstractAggregateRoot<BillingDispute> {

    @Column(nullable = false)
    private Long utilityBillId;

    /** Tenant managed by Identity and Access Management; there is no physical foreign key across contexts. */
    @Column(nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeReason reason;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingDisputeStatus status;

    @Column(length = 1000)
    private String reviewNotes;

    @Column(length = 1000)
    private String resolution;

    private Instant resolvedAt;

    public BillingDispute(Long utilityBillId, Long tenantId, DisputeReason reason, String description) {
        if (utilityBillId == null || utilityBillId <= 0 || tenantId == null || tenantId <= 0 || reason == null)
            throw new BusinessRuleException(UtilityBillingError.INVALID_BILL);
        this.utilityBillId = utilityBillId;
        this.tenantId = tenantId;
        this.reason = reason;
        this.description = description;
        this.status = BillingDisputeStatus.OPEN;
    }

    /** Announces the dispute. Call it once the dispute is saved, because the event carries its identifier. */
    public void raise() {
        registerEvent(new BillingDisputeRaisedEvent(new BillingDisputeId(getId()),
                new UtilityBillId(utilityBillId), Instant.now()));
    }

    /** OPEN to UNDER_REVIEW. The review notes are optional. */
    public void review(String reviewNotes) {
        if (status != BillingDisputeStatus.OPEN)
            throw new BusinessRuleException(UtilityBillingError.INVALID_TRANSITION);
        this.reviewNotes = (reviewNotes == null || reviewNotes.isBlank()) ? null : reviewNotes.trim();
        this.status = BillingDisputeStatus.UNDER_REVIEW;
    }

    /** Closes the dispute. A resolution text is required. */
    public void resolve(String resolution, Instant at) {
        if (status == BillingDisputeStatus.RESOLVED)
            throw new BusinessRuleException(UtilityBillingError.DISPUTE_CLOSED);
        if (resolution == null || resolution.isBlank())
            throw new BusinessRuleException(UtilityBillingError.RESOLUTION_REQUIRED);
        this.resolution = resolution.trim();
        this.resolvedAt = at;
        this.status = BillingDisputeStatus.RESOLVED;
        registerEvent(new BillingDisputeResolvedEvent(new BillingDisputeId(getId()),
                new UtilityBillId(utilityBillId), at));
    }

    public boolean isOpen() {
        return status != BillingDisputeStatus.RESOLVED;
    }
}
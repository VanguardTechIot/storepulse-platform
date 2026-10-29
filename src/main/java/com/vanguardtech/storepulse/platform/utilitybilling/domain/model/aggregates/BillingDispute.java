package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.DisputeReason;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.DisputeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** Tenant's dispute over an issued bill. The administrator reviews it and then resolves it. */
@Entity
@Table(name = "billing_disputes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BillingDispute extends AuditableAbstractAggregateRoot<BillingDispute> {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Long billId;

    private String tenantName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeReason reason;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeStatus status;

    @Column(length = 1000)
    private String reviewNotes;

    @Column(length = 1000)
    private String resolution;

    private Instant resolvedAt;

    public BillingDispute(String code, Long billId, String tenantName, DisputeReason reason, String description) {
        if (code == null || code.isBlank() || billId == null || billId <= 0 || reason == null)
            throw new BusinessRuleException(UtilityBillingError.INVALID_BILL);
        this.code = code;
        this.billId = billId;
        this.tenantName = tenantName;
        this.reason = reason;
        this.description = description;
        this.status = DisputeStatus.OPEN;
    }

    /** OPEN to UNDER_REVIEW. The review notes are optional. */
    public void startReview(String reviewNotes) {
        if (status != DisputeStatus.OPEN)
            throw new BusinessRuleException(UtilityBillingError.INVALID_TRANSITION);
        this.reviewNotes = (reviewNotes == null || reviewNotes.isBlank()) ? null : reviewNotes.trim();
        this.status = DisputeStatus.UNDER_REVIEW;
    }

    /** Closes the dispute. A resolution text is required. */
    public void resolve(String resolution, Instant at) {
        if (status == DisputeStatus.RESOLVED)
            throw new BusinessRuleException(UtilityBillingError.DISPUTE_CLOSED);
        if (resolution == null || resolution.isBlank())
            throw new BusinessRuleException(UtilityBillingError.RESOLUTION_REQUIRED);
        this.resolution = resolution.trim();
        this.resolvedAt = at;
        this.status = DisputeStatus.RESOLVED;
    }

    public boolean isOpen() {
        return status != DisputeStatus.RESOLVED;
    }
}
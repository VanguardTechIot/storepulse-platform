package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions.UtilityBillingError;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.entities.BillingItem;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.events.UtilityBillGeneratedEvent;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.events.UtilityBillIssuedEvent;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BillingPeriod;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityBillId;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityBillStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility bill (water and electricity) of one commercial unit for one period. It is born as a
 * draft, its data is verified and then the gallery administrator issues it. The breakdown that
 * supports the charge lives inside the bill.
 */
@Entity
@Table(name = "utility_bills")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UtilityBill extends AuditableAbstractAggregateRoot<UtilityBill> {

    public static final int FULL_DATA_COMPLETENESS = 100;
    public static final int PAYMENT_TERM_DAYS = 15;

    @Column(nullable = false, unique = true)
    private String code;

    /** Unit managed by Property Management; there is no physical foreign key across contexts. */
    @Column(nullable = false)
    private Long unitId;

    @Column(nullable = false)
    private String unitCode;

    private String unitName;
    private String tenantName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "billing_period_start", nullable = false)),
            @AttributeOverride(name = "endDate", column = @Column(name = "billing_period_end", nullable = false))
    })
    private BillingPeriod billingPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UtilityBillStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "utility_bill_id")
    private List<BillingItem> breakdown = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /** Percentage (0-100) of the expected readings received for the period. */
    private int dataCompleteness;

    /** The administrator verified the bill accepting incomplete data. */
    private boolean dataWarningAccepted;

    private Instant issuedAt;
    private LocalDate dueDate;

    public UtilityBill(Long unitId, String unitCode, String unitName, String tenantName,
                       BillingPeriod billingPeriod, List<BillingItem> breakdown, int dataCompleteness) {
        if (unitId == null || unitId <= 0 || unitCode == null || unitCode.isBlank() || billingPeriod == null
                || breakdown == null || breakdown.isEmpty() || dataCompleteness < 0 || dataCompleteness > 100)
            throw new BusinessRuleException(UtilityBillingError.INVALID_BILL);
        this.unitId = unitId;
        this.unitCode = unitCode;
        this.unitName = unitName;
        this.tenantName = tenantName;
        this.billingPeriod = billingPeriod;
        this.breakdown = new ArrayList<>(breakdown);
        this.totalAmount = breakdown.stream().map(BillingItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
        this.dataCompleteness = dataCompleteness;
        this.dataWarningAccepted = false;
        this.status = UtilityBillStatus.DRAFT;
        this.code = "FAC-%s-%s".formatted(billingPeriod.startDate().format(DateTimeFormatter.ofPattern("yyyyMM")),
                unitCode.replace("-", ""));
    }

    public List<BillingItem> getBreakdown() {
        return List.copyOf(breakdown);
    }

    /** Announces the bill. Call it once the bill is saved, because the event carries its identifier. */
    public void generate() {
        registerEvent(new UtilityBillGeneratedEvent(new UtilityBillId(getId()), Instant.now()));
    }

    /** DRAFT to DATA_VERIFIED. Incomplete data needs the administrator's explicit acceptance. */
    public void verifyData(boolean acceptIncompleteData) {
        if (status != UtilityBillStatus.DRAFT)
            throw new BusinessRuleException(UtilityBillingError.INVALID_TRANSITION);
        var incomplete = dataCompleteness < FULL_DATA_COMPLETENESS;
        if (incomplete && !acceptIncompleteData)
            throw new BusinessRuleException(UtilityBillingError.INCOMPLETE_DATA_NOT_ACCEPTED);
        this.dataWarningAccepted = incomplete;
        this.status = UtilityBillStatus.DATA_VERIFIED;
    }

    /** DATA_VERIFIED to ISSUED. The bill is due {@link #PAYMENT_TERM_DAYS} days after it is issued. */
    public void issue(Instant at) {
        if (status != UtilityBillStatus.DATA_VERIFIED)
            throw new BusinessRuleException(UtilityBillingError.INVALID_TRANSITION);
        this.issuedAt = at;
        this.dueDate = at.atZone(ZoneOffset.UTC).toLocalDate().plusDays(PAYMENT_TERM_DAYS);
        this.status = UtilityBillStatus.ISSUED;
        registerEvent(new UtilityBillIssuedEvent(new UtilityBillId(getId()), unitId, at));
    }

    /** ISSUED to IN_DISPUTE, when a tenant raises a dispute over the bill. */
    public void markInDispute() {
        if (status != UtilityBillStatus.ISSUED && status != UtilityBillStatus.IN_DISPUTE)
            throw new BusinessRuleException(UtilityBillingError.INVALID_TRANSITION);
        this.status = UtilityBillStatus.IN_DISPUTE;
    }

    /** IN_DISPUTE to RESOLVED, once no dispute over the bill remains open. */
    public void markResolved() {
        if (status != UtilityBillStatus.IN_DISPUTE)
            throw new BusinessRuleException(UtilityBillingError.INVALID_TRANSITION);
        this.status = UtilityBillStatus.RESOLVED;
    }
}
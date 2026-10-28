package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingCycle;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingPeriod;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Subscription of a commercial gallery. The gallery administrator is the only actor who contracts
 * it and a gallery never has two subscriptions. It stays pending until the first charge is accepted.
 */
@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {

    @Column(unique = true, nullable = false)
    private Long galleryId;

    private Long galleryAdministratorId;
    private Long planId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;

    @Embedded
    private BillingPeriod billingPeriod;

    private int monitoredUnitCount;
    private boolean autoRenewal;
    private String paymentMethodLabel;

    public Subscription(Long galleryId, Long galleryAdministratorId, SubscriptionPlan plan, BillingCycle cycle,
                        int monitoredUnitCount) {
        requireUnitsAllowed(plan, monitoredUnitCount);
        this.galleryId = galleryId;
        this.galleryAdministratorId = galleryAdministratorId;
        this.planId = plan.getId();
        this.status = SubscriptionStatus.PENDING_PAYMENT;
        this.billingCycle = cycle;
        this.monitoredUnitCount = monitoredUnitCount;
        this.autoRenewal = true;
    }

    /** The gateway accepted the charge: starts a new period. Also used to change the plan or renew. */
    public void activate(SubscriptionPlan plan, BillingCycle cycle, String paymentMethodLabel, Instant at) {
        requireUnitsAllowed(plan, monitoredUnitCount);
        this.planId = plan.getId();
        this.status = SubscriptionStatus.ACTIVE;
        this.billingCycle = cycle;
        this.billingPeriod = BillingPeriod.startingAt(at, cycle);
        this.autoRenewal = true;
        this.paymentMethodLabel = paymentMethodLabel;
    }

    /** No future charges; the subscription keeps its validity until the expiration date. */
    public void cancelRenewal() {
        requireActive();
        if (!autoRenewal) throw new BusinessRuleException(SubscriptionsError.RENEWAL_ALREADY_DISABLED);
        autoRenewal = false;
    }

    public void reactivateRenewal() {
        if (status == SubscriptionStatus.EXPIRED) throw new BusinessRuleException(SubscriptionsError.SUBSCRIPTION_EXPIRED);
        requireActive();
        if (autoRenewal) throw new BusinessRuleException(SubscriptionsError.RENEWAL_ALREADY_ENABLED);
        autoRenewal = true;
    }

    private void requireActive() {
        if (status != SubscriptionStatus.ACTIVE) throw new BusinessRuleException(SubscriptionsError.SUBSCRIPTION_NOT_ACTIVE);
    }

    private static void requireUnitsAllowed(SubscriptionPlan plan, int units) {
        if (!plan.allowsUnits(units)) throw new BusinessRuleException(SubscriptionsError.UNIT_LIMIT_EXCEEDED);
    }
}
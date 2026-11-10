package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingCycle;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.Money;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PlanCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Plan published in the catalog, tiered by the number of monitored units.
 * A {@code null} unit limit means unlimited.
 */
@Entity
@Table(name = "subscription_plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionPlan extends AuditableAbstractAggregateRoot<SubscriptionPlan> {

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PlanCode code;

    private Integer monitoredUnitLimit;
    private BigDecimal monthlyPrice;
    private BigDecimal annualPrice;
    private String currency;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "subscription_plan_features")
    @Column(name = "feature")
    private List<String> features;

    private boolean published;

    public SubscriptionPlan(PlanCode code, Integer monitoredUnitLimit, BigDecimal monthlyPrice,
                            BigDecimal annualPrice, List<String> features) {
        this.code = code;
        this.monitoredUnitLimit = monitoredUnitLimit;
        this.monthlyPrice = monthlyPrice;
        this.annualPrice = annualPrice;
        this.currency = "PEN";
        this.features = features;
        this.published = true;
    }

    public boolean allowsUnits(int count) {
        return monitoredUnitLimit == null || count <= monitoredUnitLimit;
    }

    public Money priceFor(BillingCycle cycle) {
        return new Money(cycle == BillingCycle.ANNUAL ? annualPrice : monthlyPrice, currency);
    }
}
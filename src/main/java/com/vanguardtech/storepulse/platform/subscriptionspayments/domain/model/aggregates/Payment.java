package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingCycle;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.Money;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentMethod;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Charge that supports a subscription. Immutable once the gateway resolves it. */
@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends AuditableAbstractAggregateRoot<Payment> {

    private Long subscriptionId;
    private String receiptNumber;
    private Long planId;

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private String methodLabel;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String gatewayReference;

    public Payment(Long subscriptionId, String receiptNumber, Long planId, BillingCycle billingCycle, Money amount,
                   PaymentMethod method, String methodLabel, PaymentStatus status, String gatewayReference) {
        this.subscriptionId = subscriptionId;
        this.receiptNumber = receiptNumber;
        this.planId = planId;
        this.billingCycle = billingCycle;
        this.amount = amount;
        this.method = method;
        this.methodLabel = methodLabel;
        this.status = status;
        this.gatewayReference = gatewayReference;
    }

    public boolean isAccepted() {
        return status == PaymentStatus.ACCEPTED;
    }
}
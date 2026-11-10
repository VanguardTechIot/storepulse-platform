package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Payment;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.PaymentResource;

public class PaymentResourceFromEntityAssembler {

    public static PaymentResource toResourceFromEntity(Payment payment) {
        return new PaymentResource(payment.getId(), payment.getSubscriptionId(), payment.getReceiptNumber(),
                payment.getPlanId(), payment.getBillingCycle().name(), payment.getAmount().amount(),
                payment.getAmount().currency(), payment.getMethod().name(), payment.getMethodLabel(),
                payment.getStatus().name(), payment.getGatewayReference(), payment.getCreatedAt());
    }
}
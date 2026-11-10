package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Payment;

import java.util.List;

public interface PaymentRepository {

    Payment save(Payment payment);

    /** Most recent first. */
    List<Payment> findBySubscriptionIdOrderByCreatedAtDescIdDesc(Long subscriptionId);
}
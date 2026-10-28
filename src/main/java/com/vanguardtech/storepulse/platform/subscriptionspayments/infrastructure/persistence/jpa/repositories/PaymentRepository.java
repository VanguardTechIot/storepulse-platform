package com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySubscriptionIdOrderByCreatedAtDescIdDesc(Long subscriptionId);
}
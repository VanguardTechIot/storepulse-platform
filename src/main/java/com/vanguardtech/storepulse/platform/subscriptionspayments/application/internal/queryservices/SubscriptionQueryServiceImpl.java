package com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Payment;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.SubscriptionPlan;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetAllSubscriptionPlansQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetPaymentsBySubscriptionIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetSubscriptionByGalleryIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.queries.GetSubscriptionByIdQuery;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories.PaymentRepository;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories.SubscriptionPlanRepository;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories.SubscriptionRepository;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public List<SubscriptionPlan> handle(GetAllSubscriptionPlansQuery query) {
        return planRepository.findByPublishedTrueOrderByIdAsc();
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByGalleryIdQuery query) {
        return subscriptionRepository.findByGalleryId(query.galleryId());
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByIdQuery query) {
        return subscriptionRepository.findById(query.subscriptionId());
    }

    @Override
    public List<Payment> handle(GetPaymentsBySubscriptionIdQuery query) {
        return paymentRepository.findBySubscriptionIdOrderByCreatedAtDescIdDesc(query.subscriptionId());
    }
}
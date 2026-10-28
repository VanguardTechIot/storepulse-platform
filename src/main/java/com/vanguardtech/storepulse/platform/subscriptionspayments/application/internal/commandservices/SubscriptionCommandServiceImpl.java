package com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.outboundservices.PaymentGateway;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.exceptions.SubscriptionsError;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Payment;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.SubscriptionPlan;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.CancelRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.ReactivateRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.RequestSubscriptionCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.SeedSubscriptionPlansCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentCard;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentStatus;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PlanCode;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionCommandService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.persistence.jpa.repositories.PaymentRepository;
import com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.persistence.jpa.repositories.SubscriptionPlanRepository;
import com.vanguardtech.storepulse.platform.subscriptionspayments.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    /**
     * Contracts the plan (or changes to it) and pays it. The subscription is activated only when the
     * gateway accepts the charge; a rejected charge is recorded and leaves the subscription untouched.
     */
    @Override
    public Subscription handle(RequestSubscriptionCommand command) {
        var plan = planRepository.findById(command.planId())
                .orElseThrow(() -> new ResourceNotFoundException(SubscriptionsError.PLAN_NOT_FOUND));
        var card = PaymentCard.of(command.cardNumber(), command.cardExpiry(), YearMonth.now(ZoneOffset.UTC));
        var subscription = subscriptionRepository.findByGalleryId(command.galleryId())
                .orElseGet(() -> subscriptionRepository.save(new Subscription(command.galleryId(),
                        command.galleryAdministratorId(), plan, command.billingCycle(), command.monitoredUnitCount())));

        var now = Instant.now();
        var amount = plan.priceFor(command.billingCycle());
        var charge = paymentGateway.charge(amount, card);
        paymentRepository.save(new Payment(subscription.getId(),
                charge.accepted() ? "F001-%06d".formatted(now.toEpochMilli() % 1_000_000) : null,
                plan.getId(), command.billingCycle(), amount, command.paymentMethod(), card.label(),
                charge.accepted() ? PaymentStatus.ACCEPTED : PaymentStatus.REJECTED, charge.reference()));
        if (!charge.accepted()) throw new BusinessRuleException(SubscriptionsError.PAYMENT_REJECTED);

        subscription.activate(plan, command.billingCycle(), card.label(), now);
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription handle(CancelRenewalCommand command) {
        return update(command.subscriptionId(), Subscription::cancelRenewal);
    }

    @Override
    public Subscription handle(ReactivateRenewalCommand command) {
        return update(command.subscriptionId(), Subscription::reactivateRenewal);
    }

    @Override
    public void handle(SeedSubscriptionPlansCommand command) {
        if (planRepository.count() > 0) return;
        planRepository.saveAll(List.of(
                new SubscriptionPlan(PlanCode.BASIC, 30, BigDecimal.valueOf(290), BigDecimal.valueOf(2900),
                        List.of("security_incidents", "energy_submetering", "notification_center")),
                new SubscriptionPlan(PlanCode.COMMERCIAL, 100, BigDecimal.valueOf(690), BigDecimal.valueOf(6900),
                        List.of("everything_basic", "water_telemetry", "billing_per_unit", "tenant_communication")),
                new SubscriptionPlan(PlanCode.CORPORATE, null, BigDecimal.valueOf(1490), BigDecimal.valueOf(14900),
                        List.of("everything_commercial", "multiple_galleries", "advanced_reports"))));
    }

    private Subscription update(Long subscriptionId, Consumer<Subscription> change) {
        var subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(SubscriptionsError.SUBSCRIPTION_NOT_FOUND));
        change.accept(subscription);
        return subscriptionRepository.save(subscription);
    }
}
package com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.outboundservices.PaymentGateway;
import com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.outboundservices.acl.ExternalIamService;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Payment;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.SubscriptionPlan;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.CancelRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.RequestSubscriptionCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.AdministratorId;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingCycle;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.GalleryId;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentMethod;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentStatus;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PlanCode;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.SubscriptionStatus;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories.PaymentRepository;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories.SubscriptionPlanRepository;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.repositories.SubscriptionRepository;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionCommandServiceImplTest {

    private static final GalleryId GALLERY = new GalleryId(1L);
    private static final AdministratorId ADMINISTRATOR = new AdministratorId(1L);

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private ExternalIamService externalIamService;

    @InjectMocks
    private SubscriptionCommandServiceImpl service;

    private static RequestSubscriptionCommand requestWithCard(String cardNumber) {
        return new RequestSubscriptionCommand(GALLERY, 10, 1L, BillingCycle.MONTHLY,
                PaymentMethod.CREDIT_CARD, cardNumber, "12/30");
    }

    private void givenTheBasicPlanAndANewSubscription() {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(Optional.of(ADMINISTRATOR));
        var basic = new SubscriptionPlan(PlanCode.BASIC, 30, BigDecimal.valueOf(290), BigDecimal.valueOf(2900), List.of());
        when(planRepository.findById(1L)).thenReturn(Optional.of(basic));
        when(subscriptionRepository.findByGalleryId(GALLERY)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void rejectsAUserThatIsNotAnActiveAdministrator() {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(Optional.empty());

        var error = assertThrows(BusinessRuleException.class, () -> service.handle(requestWithCard("4111111111111111")));
        assertEquals("administrator_not_active", error.getMessage());
    }

    @Test
    void hidesTheSubscriptionOfAnotherAdministrator() {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(Optional.of(new AdministratorId(2L)));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(new Subscription(GALLERY, ADMINISTRATOR,
                new SubscriptionPlan(PlanCode.BASIC, 30, BigDecimal.valueOf(290), BigDecimal.valueOf(2900), List.of()),
                BillingCycle.MONTHLY, 10)));

        var error = assertThrows(ResourceNotFoundException.class, () -> service.handle(new CancelRenewalCommand(1L)));
        assertEquals("subscription_not_found", error.getMessage());
    }

    @Test
    void rejectsAnUnknownPlan() {
        when(externalIamService.fetchAuthenticatedAdministratorId()).thenReturn(Optional.of(ADMINISTRATOR));
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        var error = assertThrows(ResourceNotFoundException.class, () -> service.handle(requestWithCard("4111111111111111")));
        assertEquals("plan_not_found", error.getMessage());
    }

    @Test
    void activatesTheSubscriptionWhenTheChargeIsAccepted() {
        givenTheBasicPlanAndANewSubscription();
        when(paymentGateway.charge(any(), any())).thenReturn(new PaymentGateway.Charge(true, "ch_1"));

        Subscription subscription = service.handle(requestWithCard("4111111111111111"));

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus());
        var payment = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(payment.capture());
        assertEquals(PaymentStatus.ACCEPTED, payment.getValue().getStatus());
    }

    @Test
    void recordsTheRejectedChargeAndLeavesTheSubscriptionPending() {
        givenTheBasicPlanAndANewSubscription();
        when(paymentGateway.charge(any(), any())).thenReturn(new PaymentGateway.Charge(false, "ch_2"));

        var error = assertThrows(BusinessRuleException.class, () -> service.handle(requestWithCard("4111111111110002")));

        assertEquals("payment_rejected", error.getMessage());
        var payment = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(payment.capture());
        assertEquals(PaymentStatus.REJECTED, payment.getValue().getStatus());
    }
}
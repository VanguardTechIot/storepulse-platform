package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.BillingCycle;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RequestSubscriptionResource(@NotNull Long galleryId, @NotNull Long galleryAdministratorId,
                                          @NotNull @PositiveOrZero Integer monitoredUnitCount, @NotNull Long planId,
                                          @NotNull BillingCycle billingCycle, @NotNull PaymentMethod paymentMethod,
                                          @NotBlank String cardNumber, @NotBlank String cardExpiry) {
}
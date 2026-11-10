package com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.RequestSubscriptionCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.valueobjects.GalleryId;
import com.vanguardtech.storepulse.platform.subscriptionspayments.interfaces.rest.resources.RequestSubscriptionResource;

public class RequestSubscriptionCommandFromResourceAssembler {

    public static RequestSubscriptionCommand toCommandFromResource(RequestSubscriptionResource resource) {
        return new RequestSubscriptionCommand(new GalleryId(resource.galleryId()), resource.monitoredUnitCount(), resource.planId(), resource.billingCycle(), resource.paymentMethod(),
                resource.cardNumber(), resource.cardExpiry());
    }
}
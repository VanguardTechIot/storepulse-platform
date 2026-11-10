package com.vanguardtech.storepulse.platform.subscriptionspayments.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.SeedSubscriptionPlansCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services.SubscriptionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Publishes the plan catalog the first time the application starts with an empty database. */
@Service("subscriptionsApplicationReadyEventHandler")
@RequiredArgsConstructor
public class ApplicationReadyEventHandler {

    private final SubscriptionCommandService subscriptionCommandService;

    @EventListener(ApplicationReadyEvent.class)
    public void on() {
        subscriptionCommandService.handle(new SeedSubscriptionPlansCommand());
    }
}
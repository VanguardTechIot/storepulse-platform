package com.vanguardtech.storepulse.platform.subscriptionspayments.domain.services;

import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.aggregates.Subscription;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.CancelRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.ReactivateRenewalCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.RequestSubscriptionCommand;
import com.vanguardtech.storepulse.platform.subscriptionspayments.domain.model.commands.SeedSubscriptionPlansCommand;

public interface SubscriptionCommandService {

    Subscription handle(RequestSubscriptionCommand command);

    Subscription handle(CancelRenewalCommand command);

    Subscription handle(ReactivateRenewalCommand command);

    void handle(SeedSubscriptionPlansCommand command);
}
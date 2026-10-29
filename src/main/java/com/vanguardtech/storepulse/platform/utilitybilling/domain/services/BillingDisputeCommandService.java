package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.RaiseBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ResolveBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ReviewBillingDisputeCommand;

public interface BillingDisputeCommandService {

    BillingDispute handle(RaiseBillingDisputeCommand command);

    BillingDispute handle(ReviewBillingDisputeCommand command);

    BillingDispute handle(ResolveBillingDisputeCommand command);
}
package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.ResolveDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.StartDisputeReviewCommand;

public interface BillingDisputeCommandService {

    BillingDispute handle(StartDisputeReviewCommand command);

    BillingDispute handle(ResolveDisputeCommand command);
}
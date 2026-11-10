package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.RaiseBillingDisputeCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.RaiseBillingDisputeResource;

public class RaiseBillingDisputeCommandFromResourceAssembler {

    public static RaiseBillingDisputeCommand toCommandFromResource(RaiseBillingDisputeResource resource) {
        return new RaiseBillingDisputeCommand(resource.utilityBillId(), resource.reason(), resource.description());
    }
}
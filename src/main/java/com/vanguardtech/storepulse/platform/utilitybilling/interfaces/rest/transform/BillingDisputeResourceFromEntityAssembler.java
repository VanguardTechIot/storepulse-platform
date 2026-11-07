package com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.transform;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.interfaces.rest.resources.BillingDisputeResource;

public class BillingDisputeResourceFromEntityAssembler {

    public static BillingDisputeResource toResourceFromEntity(BillingDispute dispute) {
        return new BillingDisputeResource(dispute.getId(), dispute.getUtilityBillId(), dispute.getTenantId(),
                dispute.getReason().name(), dispute.getDescription(), dispute.getStatus().name(),
                dispute.getReviewNotes(), dispute.getResolution(), dispute.getCreatedAt(), dispute.getResolvedAt());
    }
}
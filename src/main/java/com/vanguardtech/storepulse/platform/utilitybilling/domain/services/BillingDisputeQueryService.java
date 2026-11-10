package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputeByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputesByBillIdsQuery;

import java.util.List;
import java.util.Optional;

public interface BillingDisputeQueryService {

    Optional<BillingDispute> handle(GetBillingDisputeByIdQuery query);

    List<BillingDispute> handle(GetBillingDisputesByBillIdsQuery query);
}
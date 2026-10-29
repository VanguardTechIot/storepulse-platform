package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetAllBillingDisputesQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputeByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputesByBillIdQuery;

import java.util.List;
import java.util.Optional;

public interface BillingDisputeQueryService {

    List<BillingDispute> handle(GetAllBillingDisputesQuery query);

    Optional<BillingDispute> handle(GetBillingDisputeByIdQuery query);

    List<BillingDispute> handle(GetBillingDisputesByBillIdQuery query);
}
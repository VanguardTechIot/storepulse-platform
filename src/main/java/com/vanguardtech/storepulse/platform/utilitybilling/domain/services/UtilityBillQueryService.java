package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillsByUnitIdsQuery;

import java.util.List;
import java.util.Optional;

public interface UtilityBillQueryService {

    Optional<UtilityBill> handle(GetUtilityBillByIdQuery query);

    List<UtilityBill> handle(GetUtilityBillsByUnitIdsQuery query);
}
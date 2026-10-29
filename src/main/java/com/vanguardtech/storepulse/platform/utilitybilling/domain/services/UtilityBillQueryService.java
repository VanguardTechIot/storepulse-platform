package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetAllUtilityBillsQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillByIdQuery;

import java.util.List;
import java.util.Optional;

public interface UtilityBillQueryService {

    List<UtilityBill> handle(GetAllUtilityBillsQuery query);

    Optional<UtilityBill> handle(GetUtilityBillByIdQuery query);
}

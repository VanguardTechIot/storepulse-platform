package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetUtilityBillsByUnitIdsQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityBillQueryService;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.UtilityBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilityBillQueryServiceImpl implements UtilityBillQueryService {

    private final UtilityBillRepository billRepository;

    @Override
    public Optional<UtilityBill> handle(GetUtilityBillByIdQuery query) {
        return billRepository.findById(query.billId());
    }

    @Override
    public List<UtilityBill> handle(GetUtilityBillsByUnitIdsQuery query) {
        return billRepository.findByUnitIdInOrderByIdDesc(query.unitIds());
    }
}
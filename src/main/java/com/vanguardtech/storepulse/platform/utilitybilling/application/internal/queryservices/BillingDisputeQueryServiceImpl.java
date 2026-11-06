package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetAllBillingDisputesQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputeByIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries.GetBillingDisputesByBillIdQuery;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.BillingDisputeQueryService;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.BillingDisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillingDisputeQueryServiceImpl implements BillingDisputeQueryService {

    private final BillingDisputeRepository disputeRepository;

    @Override
    public List<BillingDispute> handle(GetAllBillingDisputesQuery query) {
        return disputeRepository.findAll();
    }

    @Override
    public Optional<BillingDispute> handle(GetBillingDisputeByIdQuery query) {
        return disputeRepository.findById(query.disputeId());
    }

    @Override
    public List<BillingDispute> handle(GetBillingDisputesByBillIdQuery query) {
        return disputeRepository.findByUtilityBillId(query.billId());
    }
}
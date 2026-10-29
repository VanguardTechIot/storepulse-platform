package com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.BillingDispute;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.BillingDisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingDisputeRepository extends JpaRepository<BillingDispute, Long> {

    List<BillingDispute> findByUtilityBillId(Long utilityBillId);

    /** Tells whether the bill still has disputes that are not resolved. */
    boolean existsByUtilityBillIdAndStatusNot(Long utilityBillId, BillingDisputeStatus status);
}
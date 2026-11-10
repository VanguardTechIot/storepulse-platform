package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries;

import java.util.List;

public record GetBillingDisputesByBillIdsQuery(List<Long> billIds) {
}
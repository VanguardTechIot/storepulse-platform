package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.queries;

import java.util.List;

public record GetUtilityBillsByUnitIdsQuery(List<Long> unitIds) {
}
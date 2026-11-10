package com.vanguardtech.storepulse.platform.safety.domain.services;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.SafetyRule;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAllSafetyRulesQuery;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetSafetyRuleByIdQuery;

import java.util.List;
import java.util.Optional;

public interface SafetyRuleQueryService {
    List<SafetyRule> handle(GetAllSafetyRulesQuery query);

    Optional<SafetyRule> handle(GetSafetyRuleByIdQuery query);
}

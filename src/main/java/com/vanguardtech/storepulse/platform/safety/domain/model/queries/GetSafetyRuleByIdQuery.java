package com.vanguardtech.storepulse.platform.safety.domain.model.queries;

public record GetSafetyRuleByIdQuery(Long safetyRuleId) {
    public GetSafetyRuleByIdQuery {
        if (safetyRuleId == null || safetyRuleId <= 0)
            throw new IllegalArgumentException("Safety rule ID must be a positive number");
    }
}

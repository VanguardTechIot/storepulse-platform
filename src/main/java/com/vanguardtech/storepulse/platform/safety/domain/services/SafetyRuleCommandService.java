package com.vanguardtech.storepulse.platform.safety.domain.services;

import com.vanguardtech.storepulse.platform.safety.domain.model.commands.SeedSafetyRulesCommand;

public interface SafetyRuleCommandService {
    void handle(SeedSafetyRulesCommand command);
}

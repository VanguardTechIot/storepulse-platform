package com.vanguardtech.storepulse.platform.safety.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.safety.domain.model.commands.SeedSafetyRulesCommand;
import com.vanguardtech.storepulse.platform.safety.domain.services.SafetyRuleCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("safetyApplicationReadyEventHandler")
@RequiredArgsConstructor
public class ApplicationReadyEventHandler {

    private final SafetyRuleCommandService safetyRuleCommandService;

    @EventListener(ApplicationReadyEvent.class)
    public void on() {
        safetyRuleCommandService.handle(new SeedSafetyRulesCommand());
    }
}

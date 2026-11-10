package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.eventhandlers;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.SeedUtilityTariffsCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityTariffCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Publishes the initial tariffs the first time the application starts with an empty database. */
@Service("utilityBillingApplicationReadyEventHandler")
@RequiredArgsConstructor
public class ApplicationReadyEventHandler {

    private final UtilityTariffCommandService tariffCommandService;

    @EventListener(ApplicationReadyEvent.class)
    public void on() {
        tariffCommandService.handle(new SeedUtilityTariffsCommand());
    }
}
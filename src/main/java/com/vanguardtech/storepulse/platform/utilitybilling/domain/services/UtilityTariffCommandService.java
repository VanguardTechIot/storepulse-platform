package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.SeedUtilityTariffsCommand;

public interface UtilityTariffCommandService {

    void handle(SeedUtilityTariffsCommand command);
}
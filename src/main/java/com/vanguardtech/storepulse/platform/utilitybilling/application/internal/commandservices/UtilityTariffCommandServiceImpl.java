package com.vanguardtech.storepulse.platform.utilitybilling.application.internal.commandservices;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityTariff;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.SeedUtilityTariffsCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.valueobjects.UtilityType;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.services.UtilityTariffCommandService;
import com.vanguardtech.storepulse.platform.utilitybilling.infrastructure.persistence.jpa.repositories.UtilityTariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilityTariffCommandServiceImpl implements UtilityTariffCommandService {

    private final UtilityTariffRepository tariffRepository;

    /**
     * Provisional rates, taken from the web application mock until the real tariffs are provided.
     * They apply to every period because they take effect from the epoch.
     */
    @Override
    public void handle(SeedUtilityTariffsCommand command) {
        if (tariffRepository.count() > 0) return;
        tariffRepository.saveAll(List.of(
                new UtilityTariff(UtilityType.WATER, new BigDecimal("4.2"), LocalDate.EPOCH),
                new UtilityTariff(UtilityType.ELECTRICITY, new BigDecimal("0.78"), LocalDate.EPOCH)));
    }
}
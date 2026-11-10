package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

import java.time.YearMonth;

/**
 * Generates the draft bill of a store of the administrator's gallery for a month. The store data
 * comes from Property Management and the bill keeps a copy of it.
 */
public record GenerateUtilityBillCommand(Long unitId, Long administratorId, YearMonth period) {
}
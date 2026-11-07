package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

import java.time.YearMonth;

/**
 * Generates the draft bill of a unit for a month. Until Property Management is available the unit
 * data travels in the command and the bill keeps a copy of it.
 */
public record GenerateUtilityBillCommand(Long unitId, String unitCode, String unitName, String tenantName,
                                         YearMonth period) {
}
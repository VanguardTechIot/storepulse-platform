package com.vanguardtech.storepulse.platform.utilitybilling.domain.services;

import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.aggregates.UtilityBill;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.GenerateUtilityBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.IssueBillCommand;
import com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands.VerifyBillDataCommand;

public interface UtilityBillCommandService {

    UtilityBill handle(GenerateUtilityBillCommand command);

    UtilityBill handle(VerifyBillDataCommand command);

    UtilityBill handle(IssueBillCommand command);
}
package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

/** Issues a bill whose data is verified. */
public record IssueBillCommand(Long billId) {
}
package com.vanguardtech.storepulse.platform.utilitybilling.domain.model.commands;

/** Issues a bill of the administrator whose data is verified. */
public record IssueUtilityBillCommand(Long utilityBillId, Long administratorId) {
}
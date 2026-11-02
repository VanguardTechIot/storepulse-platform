package com.vanguardtech.storepulse.platform.safety.interfaces.acl;

public record SafetyEvaluation(String deviceStatus, String response, boolean alertTriggered) {
}

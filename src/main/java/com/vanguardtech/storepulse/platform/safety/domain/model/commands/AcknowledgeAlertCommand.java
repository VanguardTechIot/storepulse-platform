package com.vanguardtech.storepulse.platform.safety.domain.model.commands;

public record AcknowledgeAlertCommand(Long alertId) {
    public AcknowledgeAlertCommand {
        if (alertId == null || alertId <= 0)
            throw new IllegalArgumentException("Alert ID must be a positive number");
    }
}

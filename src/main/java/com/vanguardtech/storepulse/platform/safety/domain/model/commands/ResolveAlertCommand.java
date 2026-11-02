package com.vanguardtech.storepulse.platform.safety.domain.model.commands;

public record ResolveAlertCommand(Long alertId, String resolutionNote) {
    public ResolveAlertCommand {
        if (alertId == null || alertId <= 0)
            throw new IllegalArgumentException("Alert ID must be a positive number");
        if (resolutionNote == null || resolutionNote.isBlank())
            throw new IllegalArgumentException("Resolution note is required");
    }
}

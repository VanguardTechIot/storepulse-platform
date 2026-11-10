package com.vanguardtech.storepulse.platform.monitoring.interfaces.rest.resources;

import java.util.List;

public record MonitoredResourceResource(Long resourceId, List<Long> deviceIds) {
}

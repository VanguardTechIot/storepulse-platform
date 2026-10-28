package com.vanguardtech.storepulse.platform.monitoring.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExternalResourceAssetService {

    public Optional<Long> fetchResourceIdByDeviceId(Long deviceId) {
        return Optional.empty();
    }
}

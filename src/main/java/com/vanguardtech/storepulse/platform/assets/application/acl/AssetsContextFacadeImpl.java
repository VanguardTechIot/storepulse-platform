package com.vanguardtech.storepulse.platform.assets.application.acl;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetIoTDeviceByIdQuery;
import com.vanguardtech.storepulse.platform.assets.domain.services.AssetQueryService;
import com.vanguardtech.storepulse.platform.assets.interfaces.acl.AssetsContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssetsContextFacadeImpl implements AssetsContextFacade {


    private final AssetQueryService assetQueryService;

    public AssetsContextFacadeImpl(AssetQueryService assetQueryService) {
        this.assetQueryService = assetQueryService;
    }

    @Override
    public Optional<Long> fetchResourceIdByIoTDeviceId(Long iotDeviceId) {
        return assetQueryService.handle(new GetIoTDeviceByIdQuery(iotDeviceId))
                .map(Asset::getResourceId);
    }

    @Override
    public boolean isIoTDeviceActive(Long iotDeviceId) {
        return assetQueryService.handle(new GetIoTDeviceByIdQuery(iotDeviceId))
                .map(asset -> asset.getIotDevice().isActive())
                .orElse(false);
    }
}
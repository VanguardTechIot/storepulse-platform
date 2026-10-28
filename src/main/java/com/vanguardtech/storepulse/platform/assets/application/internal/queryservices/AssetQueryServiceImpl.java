package com.vanguardtech.storepulse.platform.assets.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetAllIoTDevicesQuery;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetIoTDeviceByIdQuery;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetIoTDeviceBySerialNumberQuery;
import com.vanguardtech.storepulse.platform.assets.domain.services.AssetQueryService;
import com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssetQueryServiceImpl implements AssetQueryService {

    private final AssetRepository assetRepository;

    public AssetQueryServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public List<Asset> handle(GetAllIoTDevicesQuery query) {
        return assetRepository.findAll();
    }

    @Override
    public Optional<Asset> handle(GetIoTDeviceByIdQuery query){
        return assetRepository.findByIotDevice_Id(query.iotDeviceId());
    }

    @Override
    public Optional<Asset> handle(GetIoTDeviceBySerialNumberQuery query){
        return assetRepository.findByIotDevice_SerialNumber(query.serialNumber().trim().toUpperCase());
    }
}

package com.vanguardtech.storepulse.platform.assets.domain.services;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetAllIoTDevicesQuery;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetIoTDeviceByIdQuery;
import com.vanguardtech.storepulse.platform.assets.domain.model.queries.GetIoTDeviceBySerialNumberQuery;

import java.util.List;
import java.util.Optional;

public interface AssetQueryService {

    List<Asset> handle(GetAllIoTDevicesQuery query);
    Optional<Asset> handle(GetIoTDeviceByIdQuery query);
    Optional<Asset> handle(GetIoTDeviceBySerialNumberQuery query);

}

package com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByIotDevice_Id(Long iotDeviceId);

    Optional<Asset> findByIotDevice_SerialNumber(String serialNumber);

    boolean existsByIotDevice_SerialNumber(String serialNumber);

}

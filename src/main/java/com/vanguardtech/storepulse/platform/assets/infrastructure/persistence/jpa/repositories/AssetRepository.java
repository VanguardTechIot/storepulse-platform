package com.vanguardtech.storepulse.platform.assets.infrastructure.persistence.jpa.repositories;

import com.vanguardtech.storepulse.platform.assets.domain.model.aggregates.Asset;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    @EntityGraph(attributePaths = {"iotDevice", "iotDevice.actuators"})
    Optional<Asset> findByIotDevice_Id(Long iotDeviceId);

    @EntityGraph(attributePaths = {"iotDevice", "iotDevice.actuators"})
    List<Asset> findByResourceIdIn(Collection<Long> resourceIds);

    Optional<Asset> findByIotDevice_SerialNumber(String serialNumber);

    boolean existsByIotDevice_SerialNumber(String serialNumber);

}
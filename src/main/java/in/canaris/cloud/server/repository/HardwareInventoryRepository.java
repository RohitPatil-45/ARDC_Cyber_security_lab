package in.canaris.cloud.server.repository;


import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.server.entity.HardwareInventory;


@Repository
@Transactional
public interface HardwareInventoryRepository extends JpaRepository<HardwareInventory, Integer> {
	
	HardwareInventory findByIpAddress(String ipAddress);
	
	HardwareInventory findByDeviceIP(String deviceIP);
}

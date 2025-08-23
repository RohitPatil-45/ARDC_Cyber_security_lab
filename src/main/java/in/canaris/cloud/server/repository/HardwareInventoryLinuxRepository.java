package in.canaris.cloud.server.repository;


import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.server.entity.HardwareInventoryLinux;


@Repository
@Transactional
public interface HardwareInventoryLinuxRepository extends JpaRepository<HardwareInventoryLinux, Integer> {
	
	HardwareInventoryLinux findByIpAddress(String ip);
	
}

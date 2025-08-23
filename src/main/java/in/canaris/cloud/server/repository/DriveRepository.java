package in.canaris.cloud.server.repository;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.server.entity.DiscoverDrives;


@Repository
@Transactional
public interface DriveRepository extends JpaRepository<DiscoverDrives, Integer> {
	
	List<DiscoverDrives> findByDeviceIP(String deviceIP);

}

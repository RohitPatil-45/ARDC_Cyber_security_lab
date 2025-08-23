package in.canaris.cloud.openstack.repository;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.AvailabilityZoneInfo;
import in.canaris.cloud.openstack.entity.Flavor;
import in.canaris.cloud.openstack.entity.KeyPair;

@Repository
@Transactional
public interface AvailabilityZoneInfoRepository extends JpaRepository<AvailabilityZoneInfo, Integer> {

	List<AvailabilityZoneInfo> findByzoneName(String zoneName);
	
	@Query("SELECT DISTINCT i.zoneName FROM AvailabilityZoneInfo i")
	List<Object[]> findDistinctAvailabilityZone();



}

package in.canaris.cloud.server.repository;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.server.entity.InterfaceMonitoring;


@Repository
@Transactional
public interface InterfaceMonitoringRepository extends JpaRepository<InterfaceMonitoring, Integer> {
	
	@Query("SELECT im from InterfaceMonitoring im where im.nodeIP=:nodeIP AND im.monitoringParam='Yes'")
	List<InterfaceMonitoring> getInterfaceSummary(String nodeIP);
}

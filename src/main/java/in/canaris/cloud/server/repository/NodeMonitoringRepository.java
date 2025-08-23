package in.canaris.cloud.server.repository;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.server.entity.DiscoverDrives;
import in.canaris.cloud.server.entity.NodeMonitoring;


@Repository
@Transactional
public interface NodeMonitoringRepository extends JpaRepository<NodeMonitoring, Integer> {
	
	List<NodeMonitoring> findByNodeIP(String ip);

}

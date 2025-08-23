package in.canaris.cloud.server.repository;


import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.server.entity.DiscoverDrives;
import in.canaris.cloud.server.entity.NodeStatusHistory;


@Repository
@Transactional
public interface NodeStatusHistoryRepository extends JpaRepository<NodeStatusHistory, Integer> {
	
	List<NodeStatusHistory> findByNodeIP(String nodeIP);

	
	@Query(value = "SELECT ci.instance_name, ns.NODE_IP, ns.NODE_STATUS, ns.EVENT_TIMESTAMP FROM cloud_monitoring_data.cloud_instance ci, npm.node_status_history ns where ci.instance_ip = ns.NODE_IP "
			+ "and ns.EVENT_TIMESTAMP >= :fromDate and ns.EVENT_TIMESTAMP <= :toDate and ns.node_ip in (:ip)", nativeQuery = true)
	List<Object[]> vmStatusReportData(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("ip") String ips);

}

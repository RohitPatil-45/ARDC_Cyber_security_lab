package in.canaris.cloud.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstanceNodeHealthHistory;

@Repository
@Transactional
public interface CloudInstanceNodeHealthHistoryRepository
		extends JpaRepository<CloudInstanceNodeHealthHistory, Integer> {

//	@Query(value = "SELECT ns.VM_NAME, ci.instance_ip, ns.NODE_IP, ns.CPU_UTILIZATION, ns.MEMORY_UTILIZATION, ns.TOTAL_MEMORY, ns.USED_MEMORY, ns.FREE_MEMORY, ns.TEMPERATURE, ns.EVENT_TIMESTAMP "
//			+ "FROM cloud_instance_node_health_history ns, cloud_instance ci where ci.instance_name = ns.VM_NAME AND "
//			+ "ns.EVENT_TIMESTAMP >= :fromDate and ns.EVENT_TIMESTAMP <= :toDate and ns.VM_NAME in (:ip)", nativeQuery = true)
//	List<Object[]> vmHealthHistoryReportData(Date fromDate, Date toDate, List<String> ip);
	
	@Query(value = "SELECT VM_NAME,CPU_UTILIZATION, MEMORY_UTILIZATION, TOTAL_MEMORY,USED_MEMORY,FREE_MEMORY,EVENT_TIMESTAMP "
			+ "FROM cloud_instance_node_health_history   where "
			+ "EVENT_TIMESTAMP >= :fromDate and EVENT_TIMESTAMP <= :toDate and VM_NAME in (:ip)", nativeQuery = true)
	List<Object[]> vmHealthHistoryReportData(Date fromDate, Date toDate, List<String> ip);

	@Query(value = "SELECT ns.VM_NAME, ci.instance_ip, ns.NODE_IP, ns.CPU_UTILIZATION, ns.MEMORY_UTILIZATION, ns.TOTAL_MEMORY, ns.USED_MEMORY, ns.FREE_MEMORY, ns.TEMPERATURE, ns.EVENT_TIMESTAMP "
			+ "FROM cloud_instance_node_health_history ns, cloud_instance ci where ci.instance_name = ns.VM_NAME AND "
			+ "ns.VM_NAME = :vm", nativeQuery = true)
	List<Object[]> vmHealthHistoryReport(String vm);

	@Query(value = "SELECT ns.VM_NAME, ci.instance_ip, ns.NODE_IP, ns.CPU_UTILIZATION, ns.MEMORY_UTILIZATION, ns.TOTAL_MEMORY, ns.USED_MEMORY, ns.FREE_MEMORY, ns.TEMPERATURE, ns.EVENT_TIMESTAMP "
			+ "FROM cloud_instance_node_health_history ns, cloud_instance ci where ci.instance_name = ns.VM_NAME AND "
			+ "ns.EVENT_TIMESTAMP >= :fromDate and ns.EVENT_TIMESTAMP <= :toDate and ns.VM_NAME = :ip", nativeQuery = true)
	List<Object[]> vmHealthHistoryReportData2(Date fromDate, Date toDate, String ip);
	
	


}

package in.canaris.cloud.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import in.canaris.cloud.entity.KVMDriveDetails;

public interface KVMDriveDetailsRepository extends JpaRepository<KVMDriveDetails, Integer> {

//     Custom query to find by vmname and physicalserverip
	@Query("SELECT d FROM KVMDriveDetails d WHERE d.vmname = :vmname AND d.physicalserverip = :physicalserverip")
	KVMDriveDetails findByVmnameAndPhysicalServerIp(@Param("vmname") String vmname,
			@Param("physicalserverip") String physicalserverip);

	@Query("SELECT d FROM KVMDriveDetails d WHERE d.vmname = :vmname AND d.physicalserverip = :physicalserverip")
	List<KVMDriveDetails> findByVmnameAndPhysicalServerIp22(@Param("vmname") String vmname,
			@Param("physicalserverip") String physicalserverip);

	@Transactional
	@Modifying
	@Query("DELETE FROM KVMDriveDetails k WHERE k.vmname = :vmName AND k.physicalserverip = :serverIP")
	void deleteByVmnameAndPhysicalserverip(String vmName, String serverIP);

	@Query("SELECT d FROM KVMDriveDetails d")
	List<KVMDriveDetails> findBykVMDetails();

	@Query("SELECT d FROM KVMDriveDetails d WHERE  d.vmname in (:ip) ")
	List<KVMDriveDetails> findBykVMDetails(List<String> ip);

//	@Query(value = "SELECT d.nodeIP, d.device, d.vmname, d.bus, d.source, d.access, d.capacity, d.used, c.totalMemory, c.usedMemory, c.freeMemory " +
//            "FROM KVMDriveDetails d " +
//            "JOIN CloudInstanceNodeHealthMonitoring c ON d.nodeIP = c.physicalserverip " +
//            "WHERE d.capacity NOT LIKE '%GB'", nativeQuery = true)

//	@Query(value = "SELECT \r\n" + "    d.physicalserverip,\r\n" + "    d.device,\r\n" + "    d.vmname,\r\n"
//			+ "    d.bus,\r\n" + "    d.source,\r\n" + "    d.access,\r\n" + "    d.capacity,\r\n" + "    d.used,\r\n"
//			+ "    c.TOTAL_MEMORY,\r\n" + "    c.USED_MEMORY,\r\n" + "    c.FREE_MEMORY,\r\n"
//			+ "    c.CPU_UTILIZATION,\r\n" + "    c.MEMORY_UTILIZATION\r\n" + "FROM \r\n"
//			+ "    kvm_drive_details d\r\n" + "JOIN \r\n" + " cloud_instance_node_health_monitoring c \r\n"
//			+ "    ON d.physicalserverip = c.NODE_IP\r\n" + "WHERE \r\n"
//			+ "    d.capacity NOT LIKE '%GB'", nativeQuery = true)

	@Query(value = "SELECT \r\n" + "    d.physicalserverip,\r\n" + "    d.device,\r\n" + "    d.vmname,\r\n"
			+ "    d.bus,\r\n" + "    d.source,\r\n" + "    d.access,\r\n" + "    d.capacity,\r\n" + "    d.used,\r\n"
			+ "    c.TOTAL_MEMORY,\r\n" + "    c.USED_MEMORY,\r\n" + "    c.FREE_MEMORY,\r\n"
			+ "    c.CPU_UTILIZATION,\r\n" + "    c.MEMORY_UTILIZATION\r\n" + "FROM \r\n"
			+ "    kvm_drive_details d\r\n" + "JOIN \r\n" + " cloud_instance_node_health_monitoring c \r\n"
			+ "    ON d.vmname = c.VM_NAME\r\n", nativeQuery = true)

	List<Object[]> findDriveAndMemoryDetails();

	@Query(value = "SELECT \r\n" + "    d.physicalserverip,\r\n" + "    d.device,\r\n" + "    d.vmname,\r\n"
			+ "    d.bus,\r\n" + "    d.source,\r\n" + "    d.access,\r\n" + "    d.capacity,\r\n" + "    d.used,\r\n"
			+ "    c.TOTAL_MEMORY,\r\n" + "    c.USED_MEMORY,\r\n" + "    c.FREE_MEMORY,\r\n"
			+ "    c.CPU_UTILIZATION,\r\n" + "    c.MEMORY_UTILIZATION\r\n" + "FROM \r\n"
			+ "    kvm_drive_details d\r\n" + "JOIN \r\n" + " cloud_instance_node_health_monitoring c \r\n"
			+ "    ON d.vmname = c.VM_NAME\r\n where d.vmname in (:ip) ", nativeQuery = true)

	List<Object[]> findDriveAndMemoryDetails(List<String> ip);

	@Query(value = "SELECT physicalserverip,device, vmname, bus,source,access,capacity,used,EventTimestamp "
			+ "FROM kvm_drive_historical_log   where "
			+ "EventTimestamp >= :fromDate and EventTimestamp <= :toDate and vmname in (:ip)", nativeQuery = true)
	List<Object[]> vmDiskHistoryReportlogData(Date fromDate, Date toDate, List<String> ip);

}

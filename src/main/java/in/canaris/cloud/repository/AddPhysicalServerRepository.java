package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.AddPhysicalServer;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.PhysicalServer;

@Repository
@Transactional
public interface AddPhysicalServerRepository extends JpaRepository<AddPhysicalServer, Long> {

	@Query("SELECT DISTINCT g.id, g.serverIP,g.virtualization_type FROM AddPhysicalServer g")
	List<Object[]> getPhysicalServerIPs();

	@Query("SELECT g FROM AddPhysicalServer g WHERE g.serverIP=:ip")
	AddPhysicalServer findByserverIP(String ip);

	@Query("SELECT s.serverIP FROM AddPhysicalServer s WHERE s.virtualization_type=:virtualizationType")
	List<Object[]> getPhysicalServerIpByVirtualizationType(String virtualizationType);

	@Query("SELECT a.virtualization_type, a.status, COUNT(a) FROM AddPhysicalServer a GROUP BY a.virtualization_type, a.status")
	List<Object[]> findVirtualizationTypeCounts();

	// Add this to your repository
//    @Query("SELECT a.virtualization_type, h.used_cpu, h.total_cpu, h.used_ram, h.total_ram, h.used_disk, h.total_disk " +
//           "FROM AddPhysicalServer a " +
//           "JOIN PhysicalServerHealthMonitoring h ON a.ipAddress = h.physicalServerIp " +
//           "WHERE a.ipAddress IS NOT NULL AND h.physicalServerIp IS NOT NULL")
//    List<Object[]> findHealthDataByVirtualizationType();

	@Query(value = "SELECT a.virtualization_type, " + "h.used_cpu, h.total_cpu, " + "h.used_ram, h.total_ram, "
			+ "h.used_disk, h.total_disk " + "FROM add_physical_server a "
			+ "INNER JOIN physical_server_health_monitoring h ON a.server_ip = h.physical_server_ip "
			+ "WHERE a.server_ip IS NOT NULL AND h.physical_server_ip IS NOT NULL ORDER BY a.virtualization_type", nativeQuery = true)
	List<Object[]> findHealthDataByVirtualizationType();

//	@Query("SELECT a.virtualization_type, " + "SUM(a.used_cpu), SUM(a.total_cpu), " + "SUM(a.used_ram), SUM(a.total_ram), "
//			+ "SUM(a.used_disk), SUM(a.total_disk) " + "FROM add_physical_server a " + "GROUP BY a.virtualization_type")
//	List<Object[]> findHealthDataByVirtualizationType();

	@Query(value = "SELECT hostname, server_ip, status " + "FROM add_physical_server "
			+ "WHERE virtualization_type = :type AND status = :status", nativeQuery = true)
	List<Object[]> findServerDetailsByTypeAndStatus(@Param("type") String type, @Param("status") String status);

	@Query(value = "SELECT hostname, server_ip, status " + "FROM add_physical_server "
			+ "WHERE virtualization_type = :type", nativeQuery = true)
	List<Object[]> findServerDetailsByTypeAndAllStatus(@Param("type") String type);

	@Query(value = "SELECT hostname, server_ip, status " + "FROM add_physical_server "
			+ "WHERE status = :status", nativeQuery = true)
	List<Object[]> findServerDetailsByAllStatus(@Param("status") String status);

}

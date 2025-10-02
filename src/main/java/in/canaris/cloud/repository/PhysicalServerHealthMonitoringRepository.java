package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.PhysicalServerHealthMonitoring;

@Repository
public interface PhysicalServerHealthMonitoringRepository extends JpaRepository<PhysicalServerHealthMonitoring, Integer> {

    

	// CPU Usage
	@Query(value = "SELECT aps.hostname, aps.server_ip, h.used_cpu, h.total_cpu,h.free_cpu " +
	               "FROM physical_server_health_monitoring h " +
	               "JOIN add_physical_server aps ON h.physical_server_ip = aps.server_ip " +
	               "WHERE aps.virtualization_type = :type", nativeQuery = true)
	List<Object[]> findCpuHealthByVirtualizationType(@Param("type") String type);


	// RAM Usage
	@Query(value = "SELECT aps.hostname, aps.server_ip, h.used_ram, h.total_ram,h.free_ram " +
	               "FROM physical_server_health_monitoring h " +
	               "JOIN add_physical_server aps ON h.physical_server_ip = aps.server_ip " +
	               "WHERE aps.virtualization_type = :type", nativeQuery = true)
	List<Object[]> findRamHealthByVirtualizationType(@Param("type") String type);


	// Disk Usage
	@Query(value = "SELECT aps.hostname, aps.server_ip, h.used_disk, h.total_disk,h.free_disk " +
	               "FROM physical_server_health_monitoring h " +
	               "JOIN add_physical_server aps ON h.physical_server_ip = aps.server_ip " +
	               "WHERE aps.virtualization_type = :type", nativeQuery = true)
	List<Object[]> findDiskHealthByVirtualizationType(@Param("type") String type);

}

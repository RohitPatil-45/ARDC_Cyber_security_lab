package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.PhysicalServerHealthMonitoring;

@Repository
public interface PhysicalServerHealthMonitoringRepository extends JpaRepository<PhysicalServerHealthMonitoring, Integer> {

    @Query(value =
        "SELECT aps.virtualization_type, " +
        "       SUM(pshm.total_cpu), SUM(pshm.used_cpu), SUM(pshm.free_cpu), " +
        "       SUM(pshm.total_ram), SUM(pshm.used_ram), SUM(pshm.free_ram), " +
        "       SUM(pshm.total_disk), SUM(pshm.used_disk), SUM(pshm.free_disk) " +
        "FROM physical_server_health_monitoring pshm " +
        "JOIN add_physical_server aps ON aps.ip = pshm.physical_server_ip " +
        "GROUP BY aps.virtualization_type",
        nativeQuery = true)
    List<Object[]> fetchResourceStatsGroupedByVirtualizationType();
}

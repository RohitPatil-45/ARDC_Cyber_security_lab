package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    
    // Or use native query (alternative)
    @Query(value = "SELECT virtualization_type, status, COUNT(*) as count FROM add_physical_server GROUP BY virtualization_type, status", nativeQuery = true)
    List<Object[]> getVirtualizationStatusCounts();

}

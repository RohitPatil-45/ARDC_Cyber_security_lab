package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.PhysicalServer;

@Repository
@Transactional
public interface PhysicalServerRepository extends JpaRepository<PhysicalServer, Integer> {

	@Query("SELECT DISTINCT g.id,g.ip_address FROM PhysicalServer g")
	List<Object[]> getAllPhysicalServerip();

	@Query("SELECT DISTINCT g.ip_address FROM PhysicalServer g")
	List<String[]> getAllPhysicalServeriplist();

}

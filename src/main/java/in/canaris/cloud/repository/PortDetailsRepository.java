package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.canaris.cloud.entity.PortDetails;


public interface PortDetailsRepository extends JpaRepository<PortDetails, Integer>{
	
	@Query("SELECT MAX(p.vncPort) FROM PortDetails p")
    Integer findMaxVncPorts();
	
	@Query("SELECT MAX(p.noVncPort) FROM PortDetails p")
    Integer findMaxnoVncPort();

}

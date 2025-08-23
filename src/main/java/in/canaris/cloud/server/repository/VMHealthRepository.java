package in.canaris.cloud.server.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.server.entity.NodeHealthMonitoring;

@Repository
@Transactional
public interface VMHealthRepository extends JpaRepository<NodeHealthMonitoring, Integer> {

	@Query("SELECT e FROM NodeHealthMonitoring e WHERE e.NODE_IP=:ip")
	NodeHealthMonitoring findByNodeIP(String ip);

}

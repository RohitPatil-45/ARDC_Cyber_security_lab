package in.canaris.cloud.server.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.server.entity.NodeAvailability;

@Repository
@Transactional
public interface NodeAvailabilityRepository extends JpaRepository<NodeAvailability, Integer> {

	List<NodeAvailability> findByNodeIP(String ip);

	@Query("SELECT nv FROM NodeAvailability nv WHERE nodeIP IN :instanceIP AND nv.eventTime >= :fromDate AND nv.eventTime <= :toDate")
	List<NodeAvailability> vmAvailabilityReportData(Date fromDate, Date toDate, List<String> instanceIP);

	@Query("SELECT nv FROM NodeAvailability nv WHERE nodeIP = :instanceIP AND nv.eventTime >= :fromDate AND nv.eventTime <= :toDate")
	List<NodeAvailability> vmAvailabilityReportData2(Date fromDate, Date toDate, String instanceIP);

	@Query("SELECT nv FROM NodeAvailability nv WHERE nodeIP = :instanceIP")
	List<NodeAvailability> vmAvailabilityReport(String instanceIP);

}

package in.canaris.cloud.repository;


import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstanceDailyUsageByAgent;




@Repository
@Transactional
public interface NodeUtilizationRepository extends JpaRepository<CloudInstanceDailyUsageByAgent, Integer> {
	
//	List<CloudInstanceDailyUsageByAgent> findByNodeIP(String ip);

	@Query("SELECT nv FROM CloudInstanceDailyUsageByAgent nv WHERE vmname IN :instanceIP AND nv.eventDate >= :fromDate AND nv.eventDate <= :toDate")
	List<CloudInstanceDailyUsageByAgent> vmAvailabilityReportData(Date fromDate, Date toDate, List<String> instanceIP);
	
	
	@Query("SELECT nv FROM CloudInstanceDailyUsageByAgent nv WHERE nv.vmname=:vm AND nv.eventDate >= :fromDate AND nv.eventDate <= :toDate")
	List<CloudInstanceDailyUsageByAgent> vmAvailabilityReport(Date fromDate, Date toDate,String vm);
	
	@Query("SELECT nv FROM CloudInstanceDailyUsageByAgent nv WHERE vmname = :instanceIP")
	List<CloudInstanceDailyUsageByAgent> vmAvailabilityReportDatadetailpage(String instanceIP);

}

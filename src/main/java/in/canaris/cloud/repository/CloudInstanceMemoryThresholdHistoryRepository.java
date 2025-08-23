package in.canaris.cloud.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstanceMemoryThresholdHistory;
import in.canaris.cloud.server.entity.NodeAvailability;

@Repository
@Transactional
public interface CloudInstanceMemoryThresholdHistoryRepository
		extends JpaRepository<CloudInstanceMemoryThresholdHistory, Integer> {

	@Query("SELECT mt FROM CloudInstanceMemoryThresholdHistory mt WHERE mt.eventTimestamp >= :fDate AND mt.eventTimestamp <= :toDate"
			+ " AND mt.vmName IN (:vm)")
	List<CloudInstanceMemoryThresholdHistory> vmMemoryThresholdReportData(Date fDate, Date toDate, List<String> vm);

	@Query("SELECT mt FROM CloudInstanceMemoryThresholdHistory mt WHERE mt.eventTimestamp >= :fDate AND mt.eventTimestamp <= :toDate"
			+ " AND mt.vmName =:vm")
	List<CloudInstanceMemoryThresholdHistory> vmMemoryThresholdReportData2(Date fDate, Date toDate, String vm);

//	change below this for todays data
//	@Query("SELECT c FROM CloudInstanceMemoryThresholdHistory c WHERE MONTH(c.eventTimestamp) = MONTH(CURRENT_DATE) AND YEAR(c.eventTimestamp) = YEAR(CURRENT_DATE)")
//	@Query("SELECT c FROM CloudInstanceMemoryThresholdHistory c WHERE c.vmName IN :instances order by c.eventTimestamp desc")	
	@Query("SELECT c FROM CloudInstanceMemoryThresholdHistory c WHERE DATE(c.eventTimestamp) = CURRENT_DATE AND c.vmName IN :instances")
	List<CloudInstanceMemoryThresholdHistory> findCurrentMonthData(List<String> instances);
	
	@Query("SELECT c FROM CloudInstanceMemoryThresholdHistory c WHERE c.vmName IN :instances order by c.eventTimestamp desc")
	List<CloudInstanceMemoryThresholdHistory> findLastFiveData(List<String> instances,Pageable pageable);

	
	@Query("SELECT c FROM CloudInstanceMemoryThresholdHistory c WHERE  c.eventTimestamp >= :fromDate AND c.eventTimestamp <= :toDate AND LOWER(c.memoryStatus) IN :listSevirity AND c.vmName IN :instancesalert")
	List<CloudInstanceMemoryThresholdHistory> findfilteredData(Date fromDate, Date toDate, List<String> listSevirity,
			List<String> instancesalert);

	@Query("SELECT mt FROM CloudInstanceMemoryThresholdHistory mt WHERE mt.vmName=:vm")
	List<CloudInstanceMemoryThresholdHistory> vmMemoryThresholdReport(String vm);

}

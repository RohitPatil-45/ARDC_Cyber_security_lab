package in.canaris.cloud.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstanceCpuThresholdHistory;

@Repository
@Transactional
public interface CloudInstanceCpuThresholdHistoryRepository
		extends JpaRepository<CloudInstanceCpuThresholdHistory, Integer> {

	@Query("SELECT mt FROM CloudInstanceCpuThresholdHistory mt WHERE mt.eventTimestamp >= :fDate AND mt.eventTimestamp <= :toDate"
			+ " AND mt.vmName IN (:vm)")
	List<CloudInstanceCpuThresholdHistory> vmCpuThresholdReportData(Date fDate, Date toDate, List<String> vm);

	@Query("SELECT mt FROM CloudInstanceCpuThresholdHistory mt WHERE mt.eventTimestamp >= :fDate AND mt.eventTimestamp <= :toDate"
			+ " AND mt.vmName =:vm")
	List<CloudInstanceCpuThresholdHistory> vmCpuThresholdReportData2(Date fDate, Date toDate, String vm);

//	change below this for todays data
//	@Query("SELECT c FROM CloudInstanceCpuThresholdHistory c WHERE MONTH(c.eventTimestamp) = MONTH(CURRENT_DATE) AND YEAR(c.eventTimestamp) = YEAR(CURRENT_DATE)")
	@Query("SELECT c FROM CloudInstanceCpuThresholdHistory c WHERE DATE(c.eventTimestamp) = CURRENT_DATE AND c.vmName IN :instances")
	List<CloudInstanceCpuThresholdHistory> findCurrentMonthData(List<String> instances);

	@Query("SELECT c FROM CloudInstanceCpuThresholdHistory c WHERE  c.eventTimestamp >= :fromDate AND c.eventTimestamp <= :toDate  AND LOWER(c.cpuStatus) IN :listSevirity AND c.vmName IN :instancesalert")
	List<CloudInstanceCpuThresholdHistory> findfilteredData(Date fromDate, Date toDate, List<String> listSevirity,
			List<String> instancesalert);

	@Query("SELECT mt FROM CloudInstanceCpuThresholdHistory mt WHERE mt.vmName=:vm")
	List<CloudInstanceCpuThresholdHistory> vmCpuThresholdReport(String vm);

	@Query("SELECT c FROM CloudInstanceCpuThresholdHistory c WHERE c.vmName IN :instances ORDER BY c.eventTimestamp DESC")
	List<CloudInstanceCpuThresholdHistory> findLastFiveData(List<String> instances, Pageable pageable);
	
}

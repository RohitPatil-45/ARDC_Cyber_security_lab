package in.canaris.cloud.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceLog;
import in.canaris.cloud.entity.CloudInstanceUsage;
import in.canaris.cloud.entity.CloudInstanceUsageDaily;

@Repository
@Transactional
public interface CloudInstanceLogRepository extends JpaRepository<CloudInstanceLog, Integer> {

	@Query("SELECT log FROM CloudInstanceLog log WHERE log.event_time >=:from_date AND log.event_time<=:to_date AND log.instance_name IN :instances order by log.id desc")
	List<CloudInstanceLog> getVmActivityLogOrderByIdDesc(Date from_date, Date to_date, List<String> instances);
	
	
	@Query("SELECT log FROM CloudInstanceLog log WHERE log.event_time >=:from_date AND log.event_time<=:to_date AND log.instance_name =:instances")
	List<CloudInstanceLog> getVmActivityLog2(Date from_date, Date to_date, String instances);

	@Query("SELECT log FROM CloudInstanceUsage log WHERE log.event_time >=:from_date AND log.event_time<=:to_date AND log.instance_id IN :instance_id")
	List<CloudInstanceUsage> showVMStartStopReport(Date from_date, Date to_date, List<CloudInstance> instance_id);

	@Query(value = "SELECT ciu.instance_id, ci.instance_ip, ci.instance_name, CONCAT(\n"
			+ "        FLOOR(SUM(ciu.time_difference_sec) / (24 * 3600)), ' Days ',\n"
			+ "        FLOOR(MOD(SUM(ciu.time_difference_sec), (24 * 3600)) / 3600), ' Hours ',\n"
			+ "        FLOOR(MOD(SUM(ciu.time_difference_sec), 3600) / 60), ' Minutes ',\n"
			+ "        MOD(SUM(ciu.time_difference_sec), 60), ' Seconds'\n" + "    ) AS duration "
			+ "FROM cloud_instance_usage_daily ciu " + "JOIN cloud_instance ci "
			+ "WHERE ciu.date >= :from_date AND ciu.date <= :to_date "
			+ "AND (:instance_id IS NULL OR ci.id = :instance_id) "
			+ "AND ci.id = ciu.instance_id GROUP BY ciu.instance_id", nativeQuery = true)
	List<Object[]> showVMDailyUsageReport(@Param("from_date") Date fromDate, @Param("to_date") Date toDate,
			@Param("instance_id") CloudInstance instanceId);

	@Query(value = "SELECT ciu.instance_id, ci.instance_ip, ci.instance_name, CONCAT(\n"
			+ "        FLOOR(SUM(ciu.time_difference_sec) / (24 * 3600)), ' Days ',\n"
			+ "        FLOOR(MOD(SUM(ciu.time_difference_sec), (24 * 3600)) / 3600), ' Hours ',\n"
			+ "        FLOOR(MOD(SUM(ciu.time_difference_sec), 3600) / 60), ' Minutes ',\n"
			+ "        MOD(SUM(ciu.time_difference_sec), 60), ' Seconds'\n" + "    ) AS duration "
			+ "FROM cloud_instance_usage_daily ciu " + "JOIN cloud_instance ci "
			+ "WHERE ciu.date >= :from_date AND ciu.date <= :to_date "
			+ "AND ci.id = ciu.instance_id GROUP BY ciu.instance_id", nativeQuery = true)
	List<Object[]> showVMDailyUsageReportForAllVM(@Param("from_date") Date fromDate, @Param("to_date") Date toDate);
}

package in.canaris.cloud.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.VmLiveStatusHistory;

@Repository
@Transactional
public interface VmLiveStatusHistoryRepository extends JpaRepository<VmLiveStatusHistory, Long> {

	@Query(value = "SELECT  \r\n" + "instance_name,  \r\n" + "SUM(CASE   \r\n"
			+ "WHEN status = 'Up' THEN total_duration_seconds   \r\n" + "ELSE 0   \r\n"
			+ "END) AS uptime_seconds,  \r\n" + "SUM(CASE   \r\n"
			+ "WHEN status = 'Down' THEN total_duration_seconds   \r\n" + "ELSE 0   \r\n"
			+ "END) AS downtime_seconds    \r\n" + "FROM  \r\n" + "(   \r\n" + "SELECT   \r\n"
			+ "	instance_name,  \r\n" + "	status,  \r\n"
			+ "	COALESCE(LEAD(timestamp_epoch) OVER (PARTITION BY instance_name ORDER BY timestamp_epoch) - timestamp_epoch, 0) AS total_duration_seconds\r\n"
			+ "FROM  \r\n" + "	vm_live_status_history \r\n" + "WHERE  \r\n"
			+ "   timestamp>=:from_date AND timestamp<=:to_date  \r\n" + ") AS durations\r\n" + "GROUP BY  \r\n"
			+ "instance_name", nativeQuery = true)
	List<Object[]> showVmAvailabilityReport(@Param("from_date") Date fromDate, @Param("to_date") Date toDate);

	@Query(value = "SELECT \n" + "    instance_name, \n" + "    SUM(CASE \n"
			+ "        WHEN status = 'Up' THEN total_duration_seconds \n" + "        ELSE 0 \n"
			+ "    END) AS uptime_seconds, \n" + "    SUM(CASE \n"
			+ "        WHEN status = 'Down' THEN total_duration_seconds \n" + "        ELSE 0 \n"
			+ "    END) AS downtime_seconds \n" + "FROM \n" + "    ( \n" + "        SELECT \n"
			+ "            instance_name, \n" + "            status, \n"
			+ "            COALESCE(LEAD(timestamp_epoch) OVER (PARTITION BY instance_name ORDER BY timestamp_epoch) - timestamp_epoch, 0) AS total_duration_seconds\n"
			+ "        FROM \n" + "            vm_live_status_history \n" + "        WHERE \n"
			+ "            timestamp >= :from_date \n" + "            AND timestamp <= :to_date \n"
			+ "            AND instance_name IN (:vm_name) \n" + "    ) AS durations \n" + "GROUP BY \n"
			+ "    instance_name", nativeQuery = true)
	List<Object[]> showVmAvailabilityReport(@Param("from_date") Date fromDate, @Param("to_date") Date toDate,
			@Param("vm_name") List<String> vm_name);

	@Query(value = "select instance_name, uptime_hours, downtime_hours, uptime_percent, downtime_percent, event_timestamp from "
			+ "vm_availability where event_timestamp>=:from_date and event_timestamp<=:to_date", nativeQuery = true)
	List<Object[]> vmUptimeData(@Param("from_date") Date fromDate, @Param("to_date") Date toDate);

}

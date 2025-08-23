package in.canaris.cloud.repository;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceUsage;
import in.canaris.cloud.entity.CloudInstanceUsageDaily;
import in.canaris.cloud.entity.VPC;

@Repository
@Transactional
public interface CloudInstanceUsageDailyRepository extends JpaRepository<CloudInstanceUsageDaily, Integer> {

	@Query("SELECT d.time_difference_sec FROM CloudInstanceUsageDaily d WHERE d.instance_id=:instance_id  and d.date=:date  ")
	Long getTimeDiff(CloudInstance instance_id, Date date);

//	@Query("SELECT d.time_difference_sec FROM CloudInstanceUsageDaily d WHERE d.instance_id=:instance_id AND d.date = CURDATE() - INTERVAL 1 DAY")
//	Long getTimeDiffYesterday(CloudInstance instance_id, Date date);

	@Query(value = "SELECT d.time_difference_sec FROM cloud_instance_usage_daily d WHERE d.instance_id = :instance_id AND d.date = CURDATE() - INTERVAL 1 DAY", nativeQuery = true)
	Long getTimeDiffYesterday(CloudInstance instance_id);

	@Query("SELECT SUM(d.time_difference_sec) FROM CloudInstanceUsageDaily d WHERE d.instance_id=:instance_id")
	Long getTime(CloudInstance instance_id);

	@Modifying
	@Query("UPDATE CloudInstanceUsageDaily d SET  d.time_difference_sec=:time_difference_sec WHERE d.instance_id=:instance_id  and d.date=:date  ")
	void updateTimeDiff(long time_difference_sec, CloudInstance instance_id, Date date);

}

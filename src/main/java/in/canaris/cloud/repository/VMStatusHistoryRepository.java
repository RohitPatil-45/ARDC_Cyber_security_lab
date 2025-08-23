package in.canaris.cloud.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.CloudInstanceCpuThresholdHistory;
import in.canaris.cloud.entity.VMStatusHistory;

@Repository
@Transactional
public interface VMStatusHistoryRepository
		extends JpaRepository<VMStatusHistory, Integer> {

	@Query("SELECT mt FROM VMStatusHistory mt WHERE mt.chnageTime >= :fDate AND mt.chnageTime <= :toDate"
			+ " AND mt.instanceName IN (:vm)")
	List<VMStatusHistory> vmStatusHistoryData(Date fDate, Date toDate, List<String> vm);
	
}

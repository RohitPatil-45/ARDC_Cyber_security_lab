package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.UserLab;

public interface UserLabRepository extends JpaRepository<UserLab, Integer> {

	List<UserLab> findByscenarioId(int scenarioId);

	@Query("SELECT MAX(u.labId) FROM UserLab u")
	int findMaxLabId();

	List<UserLab> findByLabId(Long userLabId);

	List<UserLab> findByguacamoleId(int id);

//	@Transactional
//	@Modifying
//	@Query("UPDATE UserLab u SET u.Status = 'Completed' WHERE u.labId = :labId")
//	int updateStatusById(@Param("labId") Long labId);
	
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE user_lab SET status = 'Completed' WHERE lab_id = :labId", nativeQuery = true)
	int updateStatusById(@Param("labId") Long labId);

	



}

package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.UserLab;

public interface UserLabRepository extends JpaRepository<UserLab, Integer> {

	@Query("SELECT u FROM UserLab u WHERE u.scenarioId = :scenarioId AND u.username = :username")
	List<UserLab> findByScenarioIdAndUsername(@Param("scenarioId") Integer scenarioId,
			@Param("username") String username);

	@Query("SELECT MAX(u.labId) FROM UserLab u")
	Integer findMaxLabId();

	List<UserLab> findByLabId(Long userLabId);

	List<UserLab> findByguacamoleId(int id);
	
	@Query("SELECT u FROM UserLab u where u.guacamoleId=:id")
	UserLab getByProxmoxId(int id);
	
	UserLab findByInstanceName(String instanceName);

//	@Transactional
//	@Modifying
//	@Query("UPDATE UserLab u SET u.Status = 'Completed' WHERE u.labId = :labId")
//	int updateStatusById(@Param("labId") Long labId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE user_lab SET status = 'Completed' WHERE lab_id = :labId", nativeQuery = true)
	int updateStatusById(@Param("labId") Long labId);

	@Modifying
	@Transactional
	@Query("DELETE FROM UserLab u WHERE u.instanceName = :instanceName")
	void deleteByInstanceName(String instanceName);

	@Query(value = "SELECT server_ip FROM add_physical_server where virtualization_type='Docker'", nativeQuery = true)
	List<String> getPhysicalServerIPs();

	@Modifying
	@Transactional
	@Query(value = "UPDATE user_lab SET vmstate = :status WHERE instance_name = :containerName", nativeQuery = true)
	void updateStatusByLabName(@Param("containerName") String containerName, @Param("status") String status);

	@Modifying
	@Transactional
	@Query(value = "UPDATE user_lab SET status = 'InProgress' WHERE instance_name = :containerName", nativeQuery = true)
	void UpdateresetByLabName(@Param("containerName") String containerName);

	@Query("Select u FROM UserLab u WHERE u.instanceName = :containerName")
	List<UserLab> findByInstnaceName(@Param("containerName") String containerName);

	@Query("Select u FROM UserLab u WHERE u.username = :username")
	List<UserLab> findByusername(@Param("username") String username);

	@Modifying
	@Transactional
	@Query("UPDATE UserLab u SET u.lastActiveConnection = CURRENT_TIMESTAMP WHERE u.scenarioId = :scenarioId AND u.username = :username")
	int updateLastActiveConnection(@Param("scenarioId") Integer scenarioId, @Param("username") String username);

	
	
	@Modifying
	@Transactional
	@Query("UPDATE UserLab u SET u.mandatoryCommandExecuted = :status WHERE u.guacamoleId = :guacamoleId")
	void updateMandatoryCommandStatus(@Param("guacamoleId") Integer guacamoleId, @Param("status") Boolean status);

	List<UserLab> findByUsernameAndScenarioId(String username, int scenarioId);


}

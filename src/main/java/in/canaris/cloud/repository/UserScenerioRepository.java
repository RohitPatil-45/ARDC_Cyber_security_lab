package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.ChartBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.UserScenario;

public interface UserScenerioRepository extends JpaRepository<UserScenario, Integer> {

	@Query(value = "SELECT * FROM user_scenario WHERE scenario_id = :scenarioId and username = :username", nativeQuery = true)
	Optional<UserScenario> findByScenarioId(@Param("scenarioId") String scenarioId, @Param("username") String username);

//	void deleteByInstanceName(String containerName);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM user_scenario WHERE scenario_id = :scenarioId AND username = :username", nativeQuery = true)
	void deleteByScenarioIdAndUsername(@Param("scenarioId") String scenarioId, @Param("username") String scenarioIdStr);
	
	
	List<UserScenario> findByUsername(String username);
	
}

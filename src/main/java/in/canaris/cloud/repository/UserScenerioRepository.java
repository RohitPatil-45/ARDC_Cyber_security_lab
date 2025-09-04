package in.canaris.cloud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.ChartBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.UserScenario;

public interface UserScenerioRepository extends JpaRepository<UserScenario, Integer> {

	@Query(value = "SELECT * FROM user_scenario WHERE scenario_id = :scenarioId", nativeQuery = true)
	Optional<UserScenario> findByScenarioId(@Param("scenarioId") String scenarioId);

}

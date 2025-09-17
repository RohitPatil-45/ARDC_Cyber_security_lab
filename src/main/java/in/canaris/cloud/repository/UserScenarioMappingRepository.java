package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.UserScenarioMapping;

public interface UserScenarioMappingRepository extends JpaRepository<UserScenarioMapping, Integer> {

	@Modifying
	@Query(value = "INSERT INTO user_scenario_mapping (user_name, scenarioid) VALUES (:userName, :scenarioId)", nativeQuery = true)
	void insert(@Param("userName") String userName, @Param("scenarioId") Integer scenarioId);

	@Query(value = "SELECT scenarioid FROM user_scenario_mapping WHERE user_name = :userName", nativeQuery = true)
	List<Integer> findScenarioIdsByUserName(@Param("userName") String userName);

	@Query("SELECT u FROM UserScenarioMapping u WHERE u.UserName = :userName")
	List<UserScenarioMapping> findByUserName(@Param("userName") String userName);

//	List<UserScenarioMapping> findByUserName(String userName);

}

package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.entity.KVMDriveDetails;
import in.canaris.cloud.openstack.entity.Add_Scenario;

public interface ScenarioRepository extends JpaRepository<Add_Scenario, Integer> {

//	List<Add_Scenario> getView_Scenario(List<String> vmGroups);

	@Query("SELECT d FROM Add_Scenario d WHERE d.Id = :id")
	List<Add_Scenario> getView_Particular_Scenerio(@Param("id") Integer SRNO);

	@Query("SELECT a FROM Add_Scenario a WHERE a.ScenarioName IN (SELECT u.ScenarioName FROM UserScenario u)")
	List<Add_Scenario> findByUserScenario();

}

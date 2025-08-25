package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.entity.PlaylistScenario;
import in.canaris.cloud.entity.PlaylistScenarioId;
import in.canaris.cloud.openstack.entity.Add_Scenario;
import in.canaris.cloud.openstack.entity.Playlist;

public interface PlaylistsSenarioRepository extends JpaRepository<PlaylistScenario, PlaylistScenarioId> {

//	@Query("SELECT d.ScenarioId FROM PlaylistScenario d WHERE d.PlaylistId = :id")
//	List<PlaylistScenario> getSenerioID(@Param("id") Integer SRNO);

//	@Query("SELECT d.scenarioId FROM PlaylistScenario d WHERE d.playlistId = :id")
//	List<Integer> getScenarioIDs(@Param("id") Integer playlistId);

	@Query("SELECT d.scenario FROM PlaylistScenario d WHERE d.id.playlistId = :id")
	List<Add_Scenario> getScenariosByPlaylist(@Param("id") Integer playlistId);

}

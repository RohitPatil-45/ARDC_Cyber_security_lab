package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.SubPlaylist;
import in.canaris.cloud.openstack.entity.SubPlaylistScenario;

public interface SubPlaylistScenarioRepository extends JpaRepository<SubPlaylistScenario, Integer> {

	List<SubPlaylistScenario> findBySubPlaylist(SubPlaylist subPlaylist);

	@Modifying
	@Transactional
	@Query("DELETE FROM SubPlaylistScenario s WHERE s.subPlaylist.id = :subPlaylistId AND s.scenario.id = :scenarioId")
	void deleteBySubPlaylistIdAndScenarioId(@Param("subPlaylistId") Integer subPlaylistId,
			@Param("scenarioId") Integer scenarioId);

}

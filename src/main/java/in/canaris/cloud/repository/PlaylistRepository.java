package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.Add_Scenario;
import in.canaris.cloud.openstack.entity.Playlist;
import in.canaris.cloud.openstack.entity.SubPlaylist;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {

	@Query("SELECT d FROM Playlist d WHERE d.Id = :id")
	List<Playlist> getView_Particular_Scenerio(@Param("id") Integer SRNO);

	

	

}

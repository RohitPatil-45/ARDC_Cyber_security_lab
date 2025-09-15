package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.SubPlaylist;

public interface SubPlaylistRepository extends JpaRepository<SubPlaylist, Integer> {

	@Query("SELECT d FROM SubPlaylist d WHERE d.Id = :id")
	List<SubPlaylist> getView_Particular_Subplaylist(@Param("id") Integer SRNO);

}

package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.UserSubplaylistMapping;

public interface UserSubplaylistMappingRepository extends JpaRepository<UserSubplaylistMapping, Integer> {

	@Query(value = "SELECT sub_playlistid FROM user_subplaylist_mapping WHERE user_name = :userName", nativeQuery = true)
	List<Integer> findSubPlaylistIdsByUserName(@Param("userName") String userName);

	
	  @Query("SELECT u FROM UserSubplaylistMapping u WHERE u.UserName = :userName")
	List<UserSubplaylistMapping> findByUserName(@Param("userName") String userName);

//	List<UserSubplaylistMapping> findByUserName(String userName);

}

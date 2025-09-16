package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.UserPlaylistMapping;

public interface UserPlaylistMappingRepository extends JpaRepository<UserPlaylistMapping, Integer> {

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO user_playlist_maping (user_name, playlist) " + "VALUES (:userName, :playlistId) "
			+ "ON DUPLICATE KEY UPDATE playlist = :playlistId", nativeQuery = true)
	void upsertUserPlaylist(@Param("userName") String userName, @Param("playlistId") Integer playlistId);

	@Query(value = "SELECT playlist FROM user_playlist_maping WHERE user_name = :userName", nativeQuery = true)
	List<Integer> findPlaylistIdsByUserName(@Param("userName") String userName);

	@Query(value = "SELECT user_name, COUNT(playlist_id) AS playlist_count " + "FROM user_playlist_maping "
			+ "GROUP BY user_name", nativeQuery = true)
	List<Object[]> getUserPlaylistSummary();

}

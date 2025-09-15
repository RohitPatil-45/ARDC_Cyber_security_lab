package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.PlaylistItem;
import in.canaris.cloud.openstack.entity.PlaylistItem.ItemType;

public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Integer> {

	List<PlaylistItem> findByPlaylistId(int sRNO);

	boolean existsByPlaylistIdAndItemIdAndItemType(int playlistId, Integer scenarioId, ItemType scenario);

	@Transactional
	@Modifying
	@Query("DELETE FROM PlaylistItem pi WHERE pi.playlistId = :playlistId AND pi.itemType = :itemType AND pi.itemId = :itemId")
	void deleteByPlaylistIdAndItemTypeAndItemId(@Param("playlistId") int playlistId,
			@Param("itemType") PlaylistItem.ItemType itemType, @Param("itemId") int itemId);

}

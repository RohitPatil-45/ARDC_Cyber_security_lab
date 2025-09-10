package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.PlaylistItem;

public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Integer> {

	List<PlaylistItem> findByPlaylistId(int sRNO);

}

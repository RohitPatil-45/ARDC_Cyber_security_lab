package in.canaris.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.SubPlaylist;

public interface SubPlaylistRepository extends JpaRepository<SubPlaylist, Integer> {

}

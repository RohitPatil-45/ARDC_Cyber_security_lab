package in.canaris.cloud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.DepartmentMaster;
import in.canaris.cloud.openstack.entity.Playlist;

public interface DepartmentMasterRepository extends JpaRepository<DepartmentMaster,Integer> {

	

	Optional<Playlist> findByDepartmentId(Integer id);

}

package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Location;

@Repository
@Transactional
public interface LocationRepository extends JpaRepository<Location, Integer> {

	@Query("SELECT DISTINCT a.id,a.location_name FROM Location a")
	List<Object[]> getAllDClocations();

	@Query("SELECT l.vm_creation_path FROM Location l WHERE l.id=:location_id")
	List<Object[]> getVMLocationPath(int location_id);
	
	@Query("SELECT a FROM Location a WHERE a.location_name=:location")
	List<Location> findByLocationName(String location);

}

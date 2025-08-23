package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Location;
import in.canaris.cloud.entity.VMLocationPath;

@Repository
@Transactional
public interface VMLocationPathRepository extends JpaRepository<VMLocationPath, Integer> {

	@Query("SELECT DISTINCT a.id,a.vm_location_path FROM VMLocationPath a")
	List<Object[]> getAllVMlocationsPahts();


}

package in.canaris.cloud.openstack.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.Flavor;

@Repository
@Transactional
public interface flavorRepository extends JpaRepository<Flavor, Integer> {

	List<Flavor> findByflavorName(String fname);

	@Query("SELECT DISTINCT i.id, i.name FROM Flavor i")
	List<Object[]> findDistinctFlavor();

	void deleteByid(String id);

}

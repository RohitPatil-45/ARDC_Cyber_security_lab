package in.canaris.cloud.openstack.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.Instance;

@Repository
@Transactional
public interface InstanceVmRepository extends JpaRepository<Instance, Integer> {

	@Query("SELECT DISTINCT i.id, i.name FROM OpenstackSecurityGroup i")
	List<Object[]> findDistinctSecurityGroup();

	void deleteByinstanceID(String Instanceid);

}

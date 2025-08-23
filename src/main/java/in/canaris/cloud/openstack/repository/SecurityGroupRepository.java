package in.canaris.cloud.openstack.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.OpenstackSecurityGroup;

@Repository
@Transactional
public interface SecurityGroupRepository extends JpaRepository<OpenstackSecurityGroup, Integer> {

	@Query("SELECT DISTINCT i.id, i.name FROM OpenstackSecurityGroup i")
	List<Object[]> findDistinctSecurityGroup();

}

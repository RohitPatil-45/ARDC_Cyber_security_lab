package in.canaris.cloud.openstack.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.Flavor;
import in.canaris.cloud.openstack.entity.KeyPair;
import in.canaris.cloud.openstack.entity.Network;

@Repository
@Transactional
public interface NetworkRepository extends JpaRepository<Network, Integer> {

	List<Network> findByname(String name);

	@Query("SELECT DISTINCT i.id, i.name FROM Network i")
	List<Object[]> findDistinctNetwork();

}

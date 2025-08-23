package in.canaris.cloud.openstack.repository;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.Flavor;
import in.canaris.cloud.openstack.entity.KeyPair;

@Repository
@Transactional
public interface keyPairRepository extends JpaRepository<KeyPair, Integer> {

	List<KeyPair> findByname(String name);
	
	@Query("SELECT DISTINCT i.name FROM KeyPair i")
	List<Object[]> findDistinctKeyPair();


}

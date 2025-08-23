package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Price;
import in.canaris.cloud.entity.SecurityGroup;
import in.canaris.cloud.entity.VPC;

@Repository
@Transactional
public interface FirewallRepository extends JpaRepository<SecurityGroup, Integer> {
	
	@Query("SELECT DISTINCT id, security_group_name from SecurityGroup")
	List<Object[]> getFirewall();

}

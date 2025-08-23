package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.VPC;

@Repository
@Transactional
public interface VPCRepository extends JpaRepository<VPC, Integer> {
	
	@Query("SELECT DISTINCT id, vpc_name FROM VPC")
	List<Object[]> getAllVPC();

}

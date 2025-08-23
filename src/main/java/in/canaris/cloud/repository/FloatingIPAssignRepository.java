package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.canaris.cloud.entity.FloatingIPAssign;



@Repository
@Transactional
public interface FloatingIPAssignRepository extends JpaRepository<FloatingIPAssign, Integer> {
	
}

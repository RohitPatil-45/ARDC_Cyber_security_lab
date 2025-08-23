package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Plan;
import in.canaris.cloud.entity.Price;
import in.canaris.cloud.entity.VPC;

@Repository
@Transactional
public interface PlanRepository extends JpaRepository<Plan, Integer> {
	
	@Query("SELECT DISTINCT a.id,a.plan_name FROM Plan a")
	List<Object[]> getAllPlan();

}

package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Price;
import in.canaris.cloud.entity.VPC;

@Repository
@Transactional
public interface PriceRepository extends JpaRepository<Price, Integer> {
	
	@Query("SELECT id, ram, vCpu, ssd_disk, bandwidth, hourly_price FROM Price WHERE plan_id = '1'")
	List<Object[]> getSharedCpuPlan();
	
	@Query("SELECT id, ram, vCpu, ssd_disk, bandwidth, hourly_price FROM Price WHERE plan_id = '2'")
	List<Object[]> getDedicatedCpuPlan();
	
	@Query("SELECT id, ram, vCpu, ssd_disk, bandwidth, hourly_price FROM Price WHERE plan_id = '3'")
	List<Object[]> getHighMemoryPlan();
	
	@Query("SELECT DISTINCT e.ram FROM Price e")
	List<Object[]> getDistinctRAM();
	
	@Query("SELECT DISTINCT e.vCpu FROM Price e")
	List<Object[]> getDistinctCPU();
	

}

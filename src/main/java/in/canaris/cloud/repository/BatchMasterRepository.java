package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.BatchMaster;

public interface BatchMasterRepository extends JpaRepository<BatchMaster, Integer> {

	

	List<BatchMaster> findBySemester_SemesterId(int semesterId);
	

}

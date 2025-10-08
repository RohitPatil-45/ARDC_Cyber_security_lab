package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.BatchMaster;
import in.canaris.cloud.openstack.entity.CourseMaster;

public interface BatchMasterRepository extends JpaRepository<BatchMaster, Integer> {

	

	List<BatchMaster> findBySemester_SemesterId(int semesterId);

	BatchMaster findBybatchId(Long batchId);

	List<BatchMaster> findBySemesterSemesterId(int semesterId);

	

	
	

}

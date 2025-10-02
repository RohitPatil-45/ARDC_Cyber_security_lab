package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.SubjectMaster;

public interface SubjectMasterRepository extends JpaRepository<SubjectMaster, Integer> {

	

	List<SubjectMaster> findBysemester_SemesterId(Integer semesterId);

	

}

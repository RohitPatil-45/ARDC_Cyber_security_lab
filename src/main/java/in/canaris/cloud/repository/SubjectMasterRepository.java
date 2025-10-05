package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.SemesterMaster;
import in.canaris.cloud.openstack.entity.SubjectMaster;

public interface SubjectMasterRepository extends JpaRepository<SubjectMaster, Integer> {

	List<SubjectMaster> findBysemester_SemesterId(Integer semesterId);
	
	List<SubjectMaster> findBySemesterAndElective(SemesterMaster semester, boolean elective);

	
	  @Query("SELECT s FROM SubjectMaster s WHERE s.semester.semesterId = :semesterId AND s.elective = true")
	  List<SubjectMaster> findElectiveSubjectsBySemester(@Param("semesterId") Integer semesterId);
	

}

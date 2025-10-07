package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.SemesterMaster;
import in.canaris.cloud.openstack.entity.SubjectMaster;

public interface SubjectMasterRepository extends JpaRepository<SubjectMaster, Integer> {

	List<SubjectMaster> findBysemester_SemesterId(Integer semesterId);

	List<SubjectMaster> findBySemesterAndElective(SemesterMaster semester, boolean elective);

	@Query(value = "SELECT * FROM subject_master WHERE semester_id = :semesterId AND is_elective = true", nativeQuery = true)
	List<SubjectMaster> findBySemesterAndvvElective(@Param("semesterId") Integer semesterId);

	List<SubjectMaster> findByTeacher(String name);

	Optional<SubjectMaster> findBysubjectId(int subjectId);




}

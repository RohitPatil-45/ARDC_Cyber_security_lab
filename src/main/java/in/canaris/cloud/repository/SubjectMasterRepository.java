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

	@Query("SELECT COUNT(DISTINCT s.teacher) FROM SubjectMaster s WHERE s.semester.course.courseId = :courseId AND s.teacher IS NOT NULL")
	Long countTeachersByCourse(@Param("courseId") int courseId);

	@Query(value = "SELECT * FROM subject_master WHERE is_elective = true", nativeQuery = true)
	List<SubjectMaster> findBySemesterAndElectived();

	@Query("SELECT DISTINCT s.semester FROM SubjectMaster s WHERE s.semester.course.courseId = :courseId AND s.elective = true AND s.isEnabled = true")
	List<SemesterMaster> findSemestersWithElectiveSubjectsByCourseId(@Param("courseId") int courseId);

	@Query("SELECT COUNT(s) FROM SubjectMaster s WHERE s.semester.semesterId = :semesterId AND s.elective = true AND s.isEnabled = true")
	long countBySemester_SemesterIdAndElectiveTrueAndIsEnabledTrue(@Param("semesterId") int semesterId);

	

}

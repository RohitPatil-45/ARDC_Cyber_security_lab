package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.CourseMaster;
import in.canaris.cloud.openstack.entity.SemesterMaster;

public interface SemesterMasterRepository extends JpaRepository<SemesterMaster, Integer> {

	List<SemesterMaster> findByCourse_CourseId(Integer courseId);

	SemesterMaster findBysemesterId(Long semesterId);

	List<SemesterMaster> findByCourseCourseId(int courseId);

	SemesterMaster findBySemesterName(String string);

	

}

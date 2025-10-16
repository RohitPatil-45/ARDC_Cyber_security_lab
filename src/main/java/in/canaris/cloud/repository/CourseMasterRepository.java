package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.canaris.cloud.openstack.entity.CourseMaster;
import in.canaris.cloud.openstack.entity.DepartmentMaster;

public interface CourseMasterRepository extends JpaRepository<CourseMaster, Integer> {

	Optional<CourseMaster> findBycourseId(Integer id);

	List<CourseMaster> findByDepartment_DepartmentId(int deptId);

	Optional<CourseMaster> findBycourseId(Long courseId);

	List<CourseMaster> findByDepartmentDepartmentId(int deptId);

	CourseMaster findByCourseName(String string);

	

	

	

	

}

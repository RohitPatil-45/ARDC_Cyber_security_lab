package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.openstack.entity.Playlist;

@Repository
@Transactional
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

	AppUser findOneByUserName(String username);

//	findOneByUserName
	@Query("SELECT u FROM AppUser u WHERE u.userName = :username")
	List<AppUser> findByUserName(@Param("username") String username);

	@Query(value = "SELECT user_name FROM app_user WHERE user_id = :userId", nativeQuery = true)
	String getUserNameById(@Param("userId") Long userId);

	List<AppUser> findByGroupName(String groupName);

	@Query(value = "SELECT group_name FROM app_user", nativeQuery = true)
	List<AppUser> findByAllGroupName();

	@Query("SELECT u.status, COUNT(u) FROM AppUser u GROUP BY u.status")
	List<Object[]> countByStatus();

//	@Query(value = "SELECT user_name, status FROM app_user WHERE status = :status", nativeQuery = true)
//	List<Object[]> findUserNameAndStatusByStatus(@Param("status") String status);

	@Query(value = "SELECT user_name, status FROM app_user WHERE status = :status OR (:status = 'offline' AND status IS NULL)", nativeQuery = true)
	List<Object[]> findUserNameAndStatusByStatus(@Param("status") String status);

	AppUser findByuserId(Long userId);

	@Query("SELECT DISTINCT u FROM AppUser u JOIN u.userRoles ur WHERE u.semesterName.semesterId = :semesterId AND u.enabled = true AND ur.appRole.roleName = 'ROLE_USER'")
	List<AppUser> findStudentsBySemesterId(@Param("semesterId") int semesterId);

	@Query("SELECT COUNT(u) FROM AppUser u JOIN u.userRoles ur WHERE u.semesterName.semesterId = :semesterId AND u.enabled = true AND ur.appRole.roleName = 'ROLE_USER'")
	Long countStudentsBySemesterId(@Param("semesterId") int semesterId);

	@Query("SELECT DISTINCT u FROM AppUser u JOIN u.userRoles ur WHERE u.semesterName.semesterId = :semesterId AND u.enabled = true AND ur.appRole.roleName IN ('ROLE_USER')")
	List<AppUser> findOnlyStudentsBySemesterId(@Param("semesterId") int semesterId);

	@Query("SELECT COUNT(DISTINCT u) FROM AppUser u JOIN u.userRoles ur WHERE u.semesterName.semesterId = :semesterId AND u.enabled = true AND ur.appRole.roleName NOT IN ('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
	Long countOnlyStudentsBySemesterId(@Param("semesterId") int semesterId);

	@Query("SELECT u FROM AppUser u WHERE " + "(:deptId = 0 OR u.departmentName.departmentId = :deptId) AND "
			+ "(:courseId = 0 OR u.courseName.courseId = :courseId) AND "
			+ "(:semesterId = 0 OR u.semesterName.semesterId = :semesterId) AND "
			+ "(:batchId = 0 OR u.batchName.batchId = :batchId)")
	List<AppUser> findByDepartmentCourseSemesterBatch(@Param("deptId") Long deptId, @Param("courseId") Long courseId,
			@Param("semesterId") Integer semesterId, // Change from Long to Integer
			@Param("batchId") Long batchId);

	@Query(value = "SELECT u.* FROM app_user u "
			+ "LEFT JOIN department_master d ON u.department_name = d.department_id "
			+ "LEFT JOIN course_master c ON u.course_name = c.course_id "
			+ "LEFT JOIN semester_master s ON u.semester_name = s.semester_id "
			+ "LEFT JOIN batch_master b ON u.batch_name = b.batch_id "
			+ "WHERE (:deptId = 0 OR d.department_id = :deptId) " + "AND (:courseId = 0 OR c.course_id = :courseId) "
			+ "AND (:semesterId = 0 OR s.semester_id = :semesterId) "
			+ "AND (:batchId = 0 OR b.batch_id = :batchId)", nativeQuery = true)
	List<AppUser> findByDepartmentCourseSemesterBatchNative(@Param("deptId") Long deptId,
			@Param("courseId") Long courseId, @Param("semesterId") Long semesterId, @Param("batchId") Long batchId);

//	// Count students by department (ROLE_USER)
//	@Query(value = "SELECT COUNT(DISTINCT u.user_id) FROM app_user u " +
//	               "INNER JOIN user_role ur ON u.user_id = ur.user_id " +
//	               "INNER JOIN app_role r ON ur.role_id = r.role_id " +
//	               "WHERE u.department_name = :departmentId AND r.role_name = 'ROLE_USER'", 
//	       nativeQuery = true)
//	Long countStudentsByDepartment(@Param("departmentId") int departmentId);
//
//	// Count teachers by department (ROLE_TEACHER)
//	@Query(value = "SELECT COUNT(DISTINCT u.user_id) FROM app_user u " +
//	               "INNER JOIN user_role ur ON u.user_id = ur.user_id " +
//	               "INNER JOIN app_role r ON ur.role_id = r.role_id " +
//	               "WHERE u.department_name = :departmentId AND r.role_name = 'ROLE_TEACHER'", 
//	       nativeQuery = true)
//	Long countTeachersByDepartment(@Param("departmentId") int departmentId);
//
//	// Count students by course (ROLE_USER)
//	@Query(value = "SELECT COUNT(DISTINCT u.user_id) FROM app_user u " +
//	               "INNER JOIN user_role ur ON u.user_id = ur.user_id " +
//	               "INNER JOIN app_role r ON ur.role_id = r.role_id " +
//	               "WHERE u.course_name = :courseId AND r.role_name = 'ROLE_USER'", 
//	       nativeQuery = true)
//	Long countStudentsByCourse(@Param("courseId") int courseId);
//
//	// Count teachers by course (ROLE_TEACHER)
//	@Query(value = "SELECT COUNT(DISTINCT u.user_id) FROM app_user u " +
//	               "INNER JOIN user_role ur ON u.user_id = ur.user_id " +
//	               "INNER JOIN app_role r ON ur.role_id = r.role_id " +
//	               "WHERE u.course_name = :courseId AND r.role_name = 'ROLE_TEACHER'", 
//	       nativeQuery = true)
//	Long countTeachersByCourse(@Param("courseId") int courseId);

	@Query("SELECT COUNT(u) FROM AppUser u WHERE u.semesterName.semesterId = :semesterId AND EXISTS (SELECT ur FROM u.userRoles ur WHERE ur.appRole.roleName = 'ROLE_USER')")
	Long countOnlyStudentsBySemesterId(@Param("semesterId") Integer semesterId);

	@Query("SELECT u FROM AppUser u WHERE u.semesterName.semesterId = :semesterId AND EXISTS (SELECT ur FROM u.userRoles ur WHERE ur.appRole.roleName = 'ROLE_USER')")
	List<AppUser> findOnlyStudentsBySemesterId(@Param("semesterId") Integer semesterId);

	List<AppUser> findBySemesterName_SemesterId(Integer semesterId);

	@Query("SELECT DISTINCT u FROM AppUser u " + "JOIN u.userRoles ur " + "JOIN ur.appRole r "
			+ "WHERE u.departmentName.departmentId = :departmentId " + "AND r.roleName = 'ROLE_HOD'")
	List<AppUser> findHodsByDepartmentId(@Param("departmentId") Integer departmentId);

	List<AppUser> findBySemesterName_SemesterIdAndEnabledTrue(int semesterId);

	

	

	
	

	

}

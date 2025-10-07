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

	@Query(value = "SELECT user_name, status FROM app_user WHERE status = :status", nativeQuery = true)
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

}

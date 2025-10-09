package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.Switch;
import in.canaris.cloud.openstack.entity.BatchMaster;
import in.canaris.cloud.openstack.entity.CourseMaster;
import in.canaris.cloud.openstack.entity.DepartmentMaster;
import in.canaris.cloud.openstack.entity.SemesterMaster;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<AppUser, Long> {

	@Query("SELECT a.generationType, a.switch_id FROM AppUser a WHERE a.userName=:username")
	List<Object[]> getData(String username);

	List<AppUser> findByuserName(String username);

	List<AppUser> findBygroupName(String groupName);

	List<AppUser> findBymobileNo(String mobileNo);

	List<AppUser> findByemail(String email);

	@Modifying
	@Query("UPDATE AppUser user SET user.name=:name, user.email=:email, user.mobileNo=:mobileNo, user.groupName=:groupName, user.switch_id=:switch_id, "
			+ "user.generationType=:generationType WHERE user.id=:userID")
	void updateUser(String name, String email, String mobileNo, String groupName, Switch switch_id,
			String generationType, long userID);

	@Query("SELECT user FROM AppUser user WHERE user.userName=:userName")
	AppUser findByUsername(String userName);

	@Query("SELECT user FROM AppUser user WHERE user.groupName IN :groups")
	List<AppUser> findBygroups(List<String> groups);

	@Query("SELECT user FROM AppUser user WHERE user.groupName LIKE CONCAT('%', :groupName, '%')")
	List<AppUser> findByGroupNameContaining(String groupName);

	@Query("SELECT DISTINCT u FROM AppUser u JOIN u.userRoles ur JOIN ur.appRole r WHERE r.roleName = :roleName")
	List<AppUser> findUsersByRoleName(@Param("roleName") String roleName);

	@Query("SELECT DISTINCT u FROM AppUser u JOIN u.userRoles ur JOIN ur.appRole r WHERE r.roleName = :roleName AND u.groupName IN :groups")
	List<AppUser> findUsersByRoleNameAndGroups(@Param("roleName") String roleName,
			@Param("groups") List<String> groups);

	@Modifying
	@Query("UPDATE AppUser u SET u.name = :name, u.email = :email, u.mobileNo = :mobileNo, " +
	       "u.groupName = :groupName, u.switch_id = :switchId, u.generationType = :generationType, " +
	       "u.departmentName = :department, u.courseName = :course, u.semesterName = :semester, " +
	       "u.batchName = :batch WHERE u.userId = :userId")
	void updateUserWithAcademic(@Param("name") String name, @Param("email") String email, 
	                           @Param("mobileNo") String mobileNo, @Param("groupName") String groupName,
	                           @Param("switchId") Switch switchId, @Param("generationType") String generationType,
	                           @Param("department") DepartmentMaster department, 
	                           @Param("course") CourseMaster course, 
	                           @Param("semester") SemesterMaster semester,
	                           @Param("batch") BatchMaster batch,
	                           @Param("userId") Long userId);

	boolean existsByUserName(String string);

	boolean existsByEmail(String email);

	boolean existsByMobileNo(String mobileNo);

}

package in.canaris.cloud.repository;



import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.UserRole;

@Repository
@Transactional
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	@Query(value = "SELECT role_id FROM user_role WHERE user_id=:id", nativeQuery = true)
	Long findRole(@Param("id") long id);
	
	@Modifying
	@Query(value = "UPDATE user_role SET Role_Id=:roleID WHERE user_id=:id", nativeQuery = true)
	void updateUserRole(@Param("id") long id, @Param("roleID") long roleID);

	

}

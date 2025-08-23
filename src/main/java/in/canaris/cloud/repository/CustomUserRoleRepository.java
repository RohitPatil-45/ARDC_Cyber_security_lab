package in.canaris.cloud.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.UserRoledefine;

import java.util.List;

import javax.persistence.EntityManager;

@Repository
public interface CustomUserRoleRepository extends JpaRepository<UserRoledefine, Long> {

	@Query(value = "SELECT au.User_Name, ar.Role_Name " + "FROM app_user au "
			+ "JOIN user_role ur ON au.User_Id = ur.User_Id " + "JOIN app_role ar ON ur.Role_Id = ar.Role_Id "
			+ "WHERE au.User_Name = :userName", nativeQuery = true)
	List<Object[]> findUserNameAndRoleNameByUserName(@Param("userName") String userName);

}

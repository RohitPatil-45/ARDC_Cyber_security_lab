package in.canaris.cloud.repository;



import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.Switch;

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
	void updateUser(String name, String email, String mobileNo, String groupName, Switch switch_id, String generationType, long userID);
	
	@Query("SELECT user FROM AppUser user WHERE user.userName=:userName")
	AppUser findByUsername(String userName);
	
	@Query("SELECT user FROM AppUser user WHERE user.groupName IN :groups")
	List<AppUser> findBygroups(List<String> groups);
	
	@Query("SELECT user FROM AppUser user WHERE user.groupName LIKE CONCAT('%', :groupName, '%')")
	List<AppUser> findByGroupNameContaining(String groupName);
	

}

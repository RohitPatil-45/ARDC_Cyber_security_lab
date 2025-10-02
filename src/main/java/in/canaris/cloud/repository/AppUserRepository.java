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

	AppUser findByuserName(String username);

	@Query(value = "SELECT user_name FROM app_user WHERE user_id = :userId", nativeQuery = true)
	String getUserNameById(@Param("userId") Long userId);

	List<AppUser> findByGroupName(String groupName);

	@Query(value = "SELECT group_name FROM app_user", nativeQuery = true)
	List<AppUser> findByAllGroupName();

	@Query("SELECT u.status, COUNT(u) FROM AppUser u GROUP BY u.status")
    List<Object[]> countByStatus();
    
    @Query(value = "SELECT user_name, status FROM app_user WHERE status = :status", nativeQuery = true)
    List<Object[]> findUserNameAndStatusByStatus(@Param("status") String status);




}

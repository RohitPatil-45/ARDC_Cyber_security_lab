package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.CommandHistory;

public interface CommandHistoryRepository extends JpaRepository<CommandHistory, Integer> {

	@Query("SELECT ch FROM CommandHistory ch WHERE ch.ContainerName = :lab_name AND ch.Command = :command")
	List<CommandHistory> findByContainerName(@Param("lab_name") String lab_name,
			@Param("command") String command);

	@Transactional
    @Modifying
    @Query("DELETE FROM CommandHistory c WHERE c.ContainerName = :containerName")
    void deleteByContainerName(String containerName);

	@Query("SELECT ch FROM CommandHistory ch WHERE ch.ContainerName = :lab_name")
	List<CommandHistory> findByContainerName(@Param("lab_name") String lab_name);

	@Query("SELECT ch FROM CommandHistory ch WHERE ch.Command = :labcommand")
	List<CommandHistory> findByCommand(@Param("labcommand") String labcommand);

	@Query("SELECT ch FROM CommandHistory ch WHERE ContainerName =:labName and ch.Command = :labcommand")
	List<CommandHistory> findByContainerNameAndCommand(@Param("labName") String labName ,@Param("labcommand") String labcommand);

	

//	 @Query("SELECT ch FROM CommandHistory ch WHERE ch.ContainerName = :labId AND ch.Command = :command")
//	    Optional<CommandHistory> findByLabIdAndCommand(@Param("labId") String labId, @Param("command") String command);

//	@Query("SELECT ch FROM CommandHistory ch WHERE ch.ContainerId = :labId")
//	List<CommandHistory> findByLabId(String labId);

}

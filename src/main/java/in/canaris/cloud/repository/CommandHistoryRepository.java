package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.CommandHistory;

public interface CommandHistoryRepository extends JpaRepository<CommandHistory, Integer> {

	@Query("SELECT ch FROM CommandHistory ch WHERE ch.ContainerName = :templateName AND ch.Command = :command")
	List<CommandHistory> findByContainerName(@Param("templateName") String templateName,
			@Param("command") String command);

//	 @Query("SELECT ch FROM CommandHistory ch WHERE ch.ContainerName = :labId AND ch.Command = :command")
//	    Optional<CommandHistory> findByLabIdAndCommand(@Param("labId") String labId, @Param("command") String command);

//	@Query("SELECT ch FROM CommandHistory ch WHERE ch.ContainerId = :labId")
//	List<CommandHistory> findByLabId(String labId);

}

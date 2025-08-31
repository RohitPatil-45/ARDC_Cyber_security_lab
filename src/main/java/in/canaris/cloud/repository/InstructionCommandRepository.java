package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.InstructionCommand;

public interface InstructionCommandRepository extends JpaRepository<InstructionCommand, Integer> {

//	@Query("SELECT i FROM InstructionCommand i WHERE i.LabId = :id and i.IsExecuted='false' order by i.LabId asc")
//	List<InstructionCommand> findByLabId(@Param("id") String id);

//	@Query(value = "SELECT TOP 1 * FROM InstructionCommand i WHERE i.LabId = :id AND i.IsExecuted = 'false' ORDER BY i.Id ASC", nativeQuery = true)
//	List<InstructionCommand> findFirstByLabId(@Param("id") String id);

	@Query(value = "SELECT * FROM instructioncommand WHERE LabId = :labId AND IsExecuted = 'false' ORDER BY Id ASC LIMIT 1", nativeQuery = true)
	InstructionCommand findNextUnexecutedByLabId(@Param("labId") String labId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE instructioncommand SET IsExecuted = 'true' WHERE LabId = :labId AND Command = :labcommand", nativeQuery = true)
	int modifyCommandByLabId(@Param("labId") String labId, @Param("labcommand") String labcommand);

}

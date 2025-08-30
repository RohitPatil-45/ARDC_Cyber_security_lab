package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.InstructionCommand;

public interface InstructionCommandRepository extends JpaRepository<InstructionCommand, Integer> {

	@Query("SELECT i FROM InstructionCommand i WHERE i.LabId = :id and IsExecuted='false' order by asc")
	List<InstructionCommand> findByLabId(@Param("id") String id);

//	@Query(value = "SELECT TOP 1 * FROM InstructionCommand i WHERE i.LabId = :id AND i.IsExecuted = 'false' ORDER BY i.Id ASC", nativeQuery = true)
//	List<InstructionCommand> findFirstByLabId(@Param("id") String id);

}

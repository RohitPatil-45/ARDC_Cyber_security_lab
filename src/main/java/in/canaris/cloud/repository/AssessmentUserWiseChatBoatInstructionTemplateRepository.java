package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.AssessmentUserWiseChatBoatInstructionTemplate;

public interface AssessmentUserWiseChatBoatInstructionTemplateRepository
		extends JpaRepository<AssessmentUserWiseChatBoatInstructionTemplate, Integer> {

//	@Query(value = "SELECT COUNT(*) FROM assessment_userwise_chatboat_instruction_template "
//			+ "WHERE isCommandExecuted='false' AND lab_id = :labid", nativeQuery = true)
//	Integer getfalseCompletionCountsByTemplateName(@Param("labid") int labid);
//
//	@Query(value = "SELECT COUNT(*) FROM assessment_userwise_chatboat_instruction_template "
//			+ "WHERE isCommandExecuted='true' AND lab_id = :labid", nativeQuery = true)
//	Integer gettrueCompletionCountsByTemplateName(@Param("labid") int labid);

//	Integer getfalseCompletionCountsByTemplateName(int labId);
//
//	Integer gettrueCompletionCountsByTemplateName(int labId);

	// Find by labId
	List<AssessmentUserWiseChatBoatInstructionTemplate> findByLabId(Integer labId);

	// Find next unexecuted instruction by labId
	@Query("SELECT a FROM AssessmentUserWiseChatBoatInstructionTemplate a WHERE a.labId = ?1 AND a.isCommandExecuted = 'false' ORDER BY a.id ASC")
	AssessmentUserWiseChatBoatInstructionTemplate findNextUnexecutedByLabId(Integer labId);

	// Update command execution status
	@Query("UPDATE AssessmentUserWiseChatBoatInstructionTemplate a SET a.isCommandExecuted = 'true' WHERE a.labId = ?1 AND a.instructionCommand = ?2")
	int modifyCommandByLabId(Integer labId, String instructionCommand);

	// Get false completion counts
	@Query(value = "SELECT COUNT(*) FROM assessment_userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='false' AND lab_id = :labid", nativeQuery = true)
	Integer getfalseCompletionCountsByTemplateName(@Param("labid") int labid);

	@Query(value = "SELECT COUNT(*) FROM assessment_userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='true' AND lab_id = :labid", nativeQuery = true)
	Integer gettrueCompletionCountsByTemplateName(@Param("labid") int labid);
}

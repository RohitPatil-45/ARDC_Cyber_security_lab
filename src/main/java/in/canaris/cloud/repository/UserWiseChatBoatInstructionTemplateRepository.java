package in.canaris.cloud.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.UserWiseChatBoatInstructionTemplate;

public interface UserWiseChatBoatInstructionTemplateRepository
		extends JpaRepository<UserWiseChatBoatInstructionTemplate, Integer> {

//	UserWiseChatBoatInstructionTemplate findNextUnexecutedByLabId(String labId);

	@Query(value = "SELECT * FROM userwise_chatboat_instruction_template WHERE temaplate_name = :TemplateName AND isCommandExecuted = 'false' ORDER BY id ASC LIMIT 1", nativeQuery = true)
	UserWiseChatBoatInstructionTemplate findNextUnexecutedByLabId(@Param("TemplateName") String TemplateName);

	@Modifying
	@Transactional
	@Query(value = "UPDATE userwise_chatboat_instruction_template SET isCommandExecuted = 'true' WHERE temaplate_name = :TemplateName AND instruction_command = :labcommand", nativeQuery = true)
	int modifyCommandByLabId(@Param("TemplateName") String TemplateName, @Param("labcommand") String labcommand);

	List<UserWiseChatBoatInstructionTemplate> findBytemaplateName(String labName);

	@Query(value = "SELECT " + "SUM(CASE WHEN isCommandExecuted = true THEN 1 ELSE 0 END) AS true_count, "
			+ "SUM(CASE WHEN isCommandExecuted = false THEN 1 ELSE 0 END) AS false_count "
			+ "FROM userwise_chatboat_instruction_template "
			+ "WHERE template_name = :templateName", nativeQuery = true)
	List<Object[]> getCompletionCountsByTemplateName(@Param("templateName") String templateName);

	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='false' AND temaplate_name = :templateName", nativeQuery = true)
	Integer getfalseCompletionCountsByTemplateName(@Param("templateName") String templateName);

	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='true' AND temaplate_name = :templateName", nativeQuery = true)
	Integer gettrueCompletionCountsByTemplateName(@Param("templateName") String templateName);

//	UserWiseChatBoatInstructionTemplate findNextUnexecutedByLabId(String labId);

}

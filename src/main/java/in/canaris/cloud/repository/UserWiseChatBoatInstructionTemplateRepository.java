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

	@Query(value = "SELECT * FROM userwise_chatboat_instruction_template WHERE lab_id = :laabId AND isCommandExecuted = 'false' ORDER BY id ASC LIMIT 1", nativeQuery = true)
	UserWiseChatBoatInstructionTemplate findNextUnexecutedByLabId(@Param("laabId") int laabId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE userwise_chatboat_instruction_template SET isCommandExecuted = 'true' WHERE lab_id = :laabId AND instruction_command = :labcommand", nativeQuery = true)
	int modifyCommandByLabId(@Param("laabId") int laabId, @Param("labcommand") String labcommand);

	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='false' AND lab_id = :labid", nativeQuery = true)
	Integer getfalseCompletionCountsByTemplateName(@Param("labid") int labid);

	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='true' AND lab_id = :labid", nativeQuery = true)
	Integer gettrueCompletionCountsByTemplateName(@Param("labid") int labid);

	List<UserWiseChatBoatInstructionTemplate> findBylabId(int laabId);

	@Modifying
	@Transactional
	@Query("DELETE FROM UserWiseChatBoatInstructionTemplate u WHERE u.labName = :labName")
	void deleteByLabName(String labName);

	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='false' AND template_id = :templateId", nativeQuery = true)
	Integer getFalseCompletionCountsByTemplateId(@Param("templateId") int templateId);

	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='true' AND template_id = :templateId", nativeQuery = true)
	Integer getTrueCompletionCountsByTemplateId(@Param("templateId") int templateId);
	
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE userwise_chatboat_instruction_template SET isCommandExecuted = 'false' WHERE lab_name = :containerName", nativeQuery = true)
	void UpdateresetByLabName(@Param("containerName") String containerName);

	

}

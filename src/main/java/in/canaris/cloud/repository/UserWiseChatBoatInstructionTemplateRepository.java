package in.canaris.cloud.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.ChartBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.Playlist;
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
			+ "WHERE isCommandExecuted='false' AND username = :username", nativeQuery = true)
	Integer getFalseCompletionCountsByTemplateId(@Param("username") String username);

	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
			+ "WHERE isCommandExecuted='true' AND username = :username", nativeQuery = true)
	Integer getTrueCompletionCountsByTemplateId(@Param("username") String username);

//	@Modifying
//	@Transactional
//	@Query(value = "UPDATE userwise_chatboat_instruction_template SET isCommandExecuted = 'false' WHERE lab_name = :containerName", nativeQuery = true)
//	void UpdateresetByLabName(@Param("containerName") String containerName);

	@Modifying
	@Transactional
	@Query(value = "UPDATE userwise_chatboat_instruction_template  SET isCommandExecuted = 'false' WHERE lab_name = :containerName", nativeQuery = true)
	int UpdateresetByLabName(@Param("containerName") String containerName);

	@Query(value = "SELECT * FROM userwise_chatboat_instruction_template WHERE lab_name = :labName AND isCommandExecuted = 'true' ORDER BY id ASC", nativeQuery = true)
	List<UserWiseChatBoatInstructionTemplate> findExecutedByLabName(@Param("labName") String labName);

//	@Query(value = "SELECT * " + "FROM userwise_chatboat_instruction_template c "
//			+ "WHERE c.id = :templateId AND c.username = :username", nativeQuery = true)
//	List<ChartBoatInstructionTemplate> findByTemplateIdAndUserName(@Param("templateId") int templateId,
//			@Param("username") String username);

//	@Query(value = "SELECT id, commandExecutedCheckTime, instruction_command, instruction_details "
//			+ "FROM userwise_chatboat_instruction_template "
//			+ "WHERE id = :templateId AND username = :userName", nativeQuery = true)
//	List<Object[]> findByTemplateIdAndUserName(@Param("templateId") int templateId, @Param("userName") String userName);

	@Query(value = "SELECT id, commandExecutedCheckTime, instruction_command, instruction_details "
			+ "FROM userwise_chatboat_instruction_template "
			+ "WHERE temaplate_name = :temaplateName AND username = :userName", nativeQuery = true)
	List<Object[]> findByTemplateNameAndUserName(@Param("temaplateName") String temaplateName,
			@Param("userName") String userName);

	@Modifying
	@Transactional
	@Query(value = "UPDATE userwise_chatboat_instruction_template "
			+ "SET instruction_command = :command, instruction_details = :instructionDetails "
			+ "WHERE id = :id", nativeQuery = true)
	int updateInstructionById(@Param("id") int id, @Param("command") String command,
			@Param("instructionDetails") byte[] instructionDetails);

	@Query(value = "SELECT DISTINCT lab_name,username, temaplate_name "
			+ "FROM userwise_chatboat_instruction_template", nativeQuery = true)
	List<Object[]> findDistinctLabUserTemplate();

	@Query("SELECT COUNT(u) FROM UserWiseChatBoatInstructionTemplate u WHERE u.username = :userName AND u.ScenarioId = :scenarioId AND u.isCommandExecuted = 'false'")
	Integer getFalseCompletionCountsByusernameandscenarioId(@Param("userName") String userName,
			@Param("scenarioId") int scenarioId);

	@Query("SELECT COUNT(u) FROM UserWiseChatBoatInstructionTemplate u WHERE u.username = :userName AND u.ScenarioId = :scenarioId AND u.isCommandExecuted = 'true'")
	Integer getTrueCompletionCountsByusernameandscenarioId(@Param("userName") String userName,
			@Param("scenarioId") int scenarioId);

	@Query(value = "SELECT DISTINCT scenarioId FROM userwise_chatboat_instruction_template WHERE username = :userName", nativeQuery = true)
	List<Object> findByScenarioIdAndUserName(@Param("userName") String userName);

	@Query("SELECT COUNT(u) FROM UserWiseChatBoatInstructionTemplate u WHERE u.labId = :labId AND u.ScenarioId = :scenarioId AND u.isCommandExecuted = 'true'")
	Integer getTrueCompletionCountsByLabIdAndScenarioId(@Param("labId") int labId, @Param("scenarioId") int scenarioId);

	@Query("SELECT COUNT(u) FROM UserWiseChatBoatInstructionTemplate u WHERE u.labId = :labId AND u.ScenarioId = :scenarioId AND u.isCommandExecuted = 'false'")
	Integer getFalseCompletionCountsByLabIdAndScenarioId(@Param("labId") int labId,
			@Param("scenarioId") int scenarioId);

//	@Query("SELECT s.scenarioName, u.username, "
//			+ "CASE WHEN MAX(it.completionStatus) = true THEN 'Completed' ELSE 'In Progress' END, "
//			+ "COALESCE(ROUND(SUM(CASE WHEN it.completionStatus = true THEN 1 ELSE 0 END) * 100.0 / COUNT(it), 2), 0), "
//			+ "COALESCE(ROUND(SUM(CASE WHEN a.completionStatus = true THEN 1 ELSE 0 END) * 100.0 / COUNT(a), 2), 0) "
//			+ "FROM InstructionTemplate it " + "JOIN it.scenario s " + "JOIN it.user u " + "LEFT JOIN it.assessments a "
//			+ "WHERE s.subject.id = :subjectId " + "GROUP BY s.scenarioName, u.username")
//	List<Object[]> findScenarioPerformanceBySubjectId(@Param("subjectId") Integer subjectId);

//	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
//			+ "WHERE isCommandExecuted='false' and username=:username", nativeQuery = true)
//	Integer getfalseCompletionallCountsByUserName(@Param("userName") String userName);
//
//	@Query(value = "SELECT COUNT(*) FROM userwise_chatboat_instruction_template "
//			+ "WHERE isCommandExecuted='true' and username=:username ", nativeQuery = true)
//	Integer gettrueCompletionallCountsByUserName(@Param("userName") String userName);

}

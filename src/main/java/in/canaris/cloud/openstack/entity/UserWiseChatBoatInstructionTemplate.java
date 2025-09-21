package in.canaris.cloud.openstack.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "userwise_chatboat_instruction_template")
public class UserWiseChatBoatInstructionTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "template_id", nullable = false)
    private int templateId;

    @Column(name = "temaplate_name", nullable = false, length = 50)
    private String temaplateName;

    @Column(name = "instruction_command", nullable = false, length = 50)
    private String instructionCommand;

    @Lob
    @Column(name = "instruction_details", nullable = false)
    private byte[] instructionDetails;

    @Column(name = "lab_id")
    private Integer labId;

    @Column(name = "lab_name", length = 50)
    private String labName;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "isCommandExecuted", length = 50)
    private String isCommandExecuted;

    @Column(name = "commandExecutedCheckTime")
    private Timestamp commandExecutedCheckTime;
    
    @Column(name = "scenarioId", length = 50)
    private int ScenarioId;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getTemaplateName() {
        return temaplateName;
    }

    public void setTemaplateName(String temaplateName) {
        this.temaplateName = temaplateName;
    }

    public String getInstructionCommand() {
        return instructionCommand;
    }

    public void setInstructionCommand(String instructionCommand) {
        this.instructionCommand = instructionCommand;
    }

    public byte[] getInstructionDetails() {
        return instructionDetails;
    }

    public void setInstructionDetails(byte[] instructionDetails) {
        this.instructionDetails = instructionDetails;
    }

    public Integer getLabId() {
        return labId;
    }

    public void setLabId(Integer labId) {
        this.labId = labId;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIsCommandExecuted() {
        return isCommandExecuted;
    }

    public void setIsCommandExecuted(String isCommandExecuted) {
        this.isCommandExecuted = isCommandExecuted;
    }

    public Timestamp getCommandExecutedCheckTime() {
        return commandExecutedCheckTime;
    }

    public void setCommandExecutedCheckTime(Timestamp commandExecutedCheckTime) {
        this.commandExecutedCheckTime = commandExecutedCheckTime;
    }

	public int getScenarioId() {
		return ScenarioId;
	}

	public void setScenarioId(int scenarioId) {
		ScenarioId = scenarioId;
	}
    
    
}

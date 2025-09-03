package in.canaris.cloud.openstack.entity;

import javax.persistence.*;

@Entity
@Table(name = "chatboat_instruction_template")
public class ChartBoatInstructionTemplate {

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

	// === Getters and Setters ===

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


}

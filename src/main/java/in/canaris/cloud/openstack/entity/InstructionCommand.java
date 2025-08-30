package in.canaris.cloud.openstack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "InstructionCommand")
public class InstructionCommand {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "LabId", nullable = false, length = 255)
	private String LabId;
	
	@Column(name = "Instruction", nullable = false, length = 255)
	private String Instruction;
	
	@Column(name = "Command", nullable = false, length = 255)
	private String Command;
	
	@Column(name = "IsExecuted", nullable = false, length = 255)
	private String IsExecuted;
	
	@Column(name = "EventTimeStamp")
	private Timestamp EventTimeStamp;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getLabId() {
		return LabId;
	}

	public void setLabId(String labId) {
		LabId = labId;
	}

	public String getInstruction() {
		return Instruction;
	}

	public void setInstruction(String instruction) {
		Instruction = instruction;
	}

	public String getCommand() {
		return Command;
	}

	public void setCommand(String command) {
		Command = command;
	}

	public String getIsExecuted() {
		return IsExecuted;
	}

	public void setIsExecuted(String isExecuted) {
		IsExecuted = isExecuted;
	}

	public Timestamp getEventTimeStamp() {
		return EventTimeStamp;
	}

	public void setEventTimeStamp(Timestamp eventTimeStamp) {
		EventTimeStamp = eventTimeStamp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}

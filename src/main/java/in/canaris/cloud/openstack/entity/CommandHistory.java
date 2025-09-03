package in.canaris.cloud.openstack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CommandHistory")
public class CommandHistory {
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "ContainerName", nullable = false, length = 255)
	private String ContainerName;
	
	@Column(name = "Command", nullable = false, length = 255)
	private String Command;
	
	
	@Column(name = "EventTimestamp")
	private Timestamp EventTimestamp;
	



	public int getId() {
		return Id;
	}


	public void setId(int id) {
		Id = id;
	}


	public String getContainerName() {
		return ContainerName;
	}


	public void setContainerName(String containerName) {
		ContainerName = containerName;
	}


	public String getCommand() {
		return Command;
	}


	public void setCommand(String command) {
		Command = command;
	}


	public Timestamp getEventTimestamp() {
		return EventTimestamp;
	}


	public void setEventTimestamp(Timestamp eventTimestamp) {
		EventTimestamp = eventTimestamp;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	
	

}

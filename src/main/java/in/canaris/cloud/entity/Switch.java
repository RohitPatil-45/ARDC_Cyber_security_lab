package in.canaris.cloud.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "switch_master")
public class Switch implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;
	
	@Column
	private String switch_name;
	
	@Column
	private String connection_type;
	
	@Column
	private String interface_description;
	
	@Column(name="physical_server_ip")
	private String physicalServerIP;
	
	@Column
	private String notes;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSwitch_name() {
		return switch_name;
	}

	public void setSwitch_name(String switch_name) {
		this.switch_name = switch_name;
	}

	public String getConnection_type() {
		return connection_type;
	}

	public void setConnection_type(String connection_type) {
		this.connection_type = connection_type;
	}

	public String getInterface_description() {
		return interface_description;
	}

	public void setInterface_description(String interface_description) {
		this.interface_description = interface_description;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getPhysicalServerIP() {
		return physicalServerIP;
	}

	public void setPhysicalServerIP(String physicalServerIP) {
		this.physicalServerIP = physicalServerIP;
	}
	
}

package in.canaris.cloud.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "location_master")
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column
	private String location_name;

	@Column
	private String description;

	@Column
	private String vm_creation_path;

	@Column
	private String physical_server_iP;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocation_name() {
		return location_name;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVm_creation_path() {
		return vm_creation_path;
	}

	public void setVm_creation_path(String vm_creation_path) {
		this.vm_creation_path = vm_creation_path;
	}

	public String getPhysical_server_iP() {
		return physical_server_iP;
	}

	public void setPhysical_server_iP(String physical_server_iP) {
		this.physical_server_iP = physical_server_iP;
	}

}

package in.canaris.cloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "VMLocationPath")
public class VMLocationPath {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column
	private String vm_location_path;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVm_location_path() {
		return vm_location_path;
	}

	public void setVm_location_path(String vm_location_path) {
		this.vm_location_path = vm_location_path;
	}
	
	
	

}

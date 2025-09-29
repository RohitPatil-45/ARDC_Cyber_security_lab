package in.canaris.cloud.openstack.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "department_master")
public class DepartmentMaster {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "department_id", updatable = false, nullable = false)
	private int departmentId;

	@Column(name = "department_name", nullable = false, length = 255)
	private String departmentName;
	
	@Column(name = "department_code", nullable = false, length = 255)
	private String departmentCode;
	
	@Column(name = "isEnabled", nullable = false, length = 255)
	private boolean is_enabled;
	
	@Column(name = "created_at", nullable = false, length = 255)
	private Timestamp created_at;
	
	
	@Column(name = "updated_at", nullable = false, length = 255)
	private Timestamp updatedAt;
	
	
	@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CourseMaster> courses = new ArrayList<>();


	public int getDepartmentId() {
		return departmentId;
	}


	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}


	public String getDepartmentName() {
		return departmentName;
	}


	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	


	public String getDepartmentCode() {
		return departmentCode;
	}


	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}


	public boolean isIs_enabled() {
		return is_enabled;
	}


	public void setIs_enabled(boolean is_enabled) {
		this.is_enabled = is_enabled;
	}

	


	public Timestamp getCreated_at() {
		return created_at;
	}


	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}


	public Timestamp getUpdatedAt() {
		return updatedAt;
	}


	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public List<CourseMaster> getCourses() {
		return courses;
	}


	public void setCourses(List<CourseMaster> courses) {
		this.courses = courses;
	}
	
	


}

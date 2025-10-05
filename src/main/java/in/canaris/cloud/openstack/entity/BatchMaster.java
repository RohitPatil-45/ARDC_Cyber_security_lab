package in.canaris.cloud.openstack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity
@Table(name = "batch_master")
public class BatchMaster {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "batch_id")
	private int batchId;

	@Column(name = "batch_name", length = 100)
	private String batchName;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "semester_id", referencedColumnName = "semester_id")
//	private SemesterMaster semester;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "semester_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private SemesterMaster semester;

	@Column(name = "is_enabled")
	private boolean isEnabled;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "updated_at")
	private Timestamp updatedAt;

	public int getBatchId() {
		return batchId;
	}

	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public SemesterMaster getSemester() {
		return semester;
	}

	public void setSemester(SemesterMaster semester) {
		this.semester = semester;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
	


}

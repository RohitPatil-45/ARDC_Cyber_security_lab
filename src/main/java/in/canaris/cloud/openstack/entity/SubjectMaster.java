package in.canaris.cloud.openstack.entity;

import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "subject_master")
public class SubjectMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subject_id", updatable = false, nullable = false)
	private int subjectId;

	@Column(name = "subject_name", length = 100)
	private String subjectName;

	// Foreign Key → SemesterMaster
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "semester_id", referencedColumnName = "semester_id")
	private SemesterMaster semester;

	@Column(name = "is_enabled", nullable = false)
	private boolean isEnabled = true;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@Column(name = "subject_code",  length = 50)
	private String subjectCode;

	// Getters & Setters
	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
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

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
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

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
}

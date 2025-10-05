package in.canaris.cloud.openstack.entity;

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
@Table(name = "elective_subject")
public class ElectiveSubject {
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "elective_subject_name", length = 255)
	private String electiveSubjectName;
	
	
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "semester_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private SemesterMaster semester;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getElectiveSubjectName() {
		return electiveSubjectName;
	}

	public void setElectiveSubjectName(String electiveSubjectName) {
		this.electiveSubjectName = electiveSubjectName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public SemesterMaster getSemester() {
		return semester;
	}

	public void setSemester(SemesterMaster semester) {
		this.semester = semester;
	}
	
	
	
	
	

}

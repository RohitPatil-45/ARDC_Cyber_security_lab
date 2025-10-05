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
@Table(name = "elective_subject_user_mapping")
public class ElectvieSubjectUserMapping {
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "elective_subject_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private SubjectMaster elective;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "semester_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private SemesterMaster semester;
	
	
	@Column
	private String userName;


	public int getId() {
		return Id;
	}


	public void setId(int id) {
		Id = id;
	}
	

	public SubjectMaster getElective() {
		return elective;
	}


	public void setElective(SubjectMaster elective) {
		this.elective = elective;
	}


	public SemesterMaster getSemester() {
		return semester;
	}


	public void setSemester(SemesterMaster semester) {
		this.semester = semester;
	}

	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	



}

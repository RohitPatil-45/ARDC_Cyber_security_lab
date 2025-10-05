package in.canaris.cloud.openstack.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "semester_master")
public class SemesterMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_id", updatable = false, nullable = false)
    private int semesterId;

    @Column(name = "semester_name", length = 50)
    private String semesterName;
    
    @Column(name = "semester_code", length = 50)
    private String semesterCode;

    // Foreign Key → CourseMaster
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id",  referencedColumnName = "course_id")
    private CourseMaster course;

    @Column(name = "is_enabled")
    private boolean isEnabled = true;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // 👇 Relationship with Subjects (One semester can have many subjects)
    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SubjectMaster> subjects = new ArrayList<>();
    
    
    @Column(name = "is_elective")
    private boolean elective;


    // Getters & Setters
    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
    
    public String getSemesterCode() {
		return semesterCode;
	}

	public void setSemesterCode(String semesterCode) {
		this.semesterCode = semesterCode;
	}

	public CourseMaster getCourse() {
        return course;
    }

    public void setCourse(CourseMaster course) {
        this.course = course;
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

    public List<SubjectMaster> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectMaster> subjects) {
        this.subjects = subjects;
    }

	public boolean isElective() {
		return elective;
	}

	public void setElective(boolean elective) {
		this.elective = elective;
	}
    
   


	
	
    
    
}

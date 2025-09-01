package in.canaris.cloud.openstack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "scenariocomments")
public class ScenarioComments {
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "scenario_id", nullable = false)
    private Long scenarioId;
    
    @Column(name = "comment", nullable = false, length = 1000)
    private String comment;
    
    @Column(name = "create_by")
    private String createBy;

    
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    
   

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreateBy() {
	    return createBy;
	}

	public void setCreateBy(String createBy) {
	    this.createBy = createBy;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}


    

}

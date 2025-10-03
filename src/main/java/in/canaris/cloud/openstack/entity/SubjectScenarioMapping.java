package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subject_scenario_maping")
public class SubjectScenarioMapping {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "subject", nullable = false, length = 255)
	private int subject;

	@Column(name = "scenarioid", nullable = false, length = 255)
	private int scenarioId;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getSubject() {
		return subject;
	}

	public void setSubject(int subject) {
		this.subject = subject;
	}

	public int getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	

}

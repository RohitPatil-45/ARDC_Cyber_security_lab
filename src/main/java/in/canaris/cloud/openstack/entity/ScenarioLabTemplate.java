package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

 
@Entity
@Table(name = "scenario_lab_template")
public class ScenarioLabTemplate {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private int id;
	
	@Column(name = "scenarioId", nullable = false, length = 255)
	private int scenarioId;

	@Column(name = "scenario_name", nullable = false, length = 255)
	private String scenarioName;
	
	@Column(name = "template_name", nullable = false, length = 255)
	private String templateName;
	
	@Column(name = "template_id", nullable = false, length = 255)
	private int templateId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	


}

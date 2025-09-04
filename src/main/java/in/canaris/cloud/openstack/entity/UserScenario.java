package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_scenario")
public class UserScenario {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "username", nullable = false, length = 255)
	private String username;

	@Column(name = "scenario_id", nullable = false, length = 255)
	private String ScenarioId;

	@Column(name = "scenario_name", nullable = false, length = 255)
	private String ScenarioName;

	@Column(name = "status", nullable = false, length = 255)
	private String Status;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getScenarioId() {
		return ScenarioId;
	}

	public void setScenarioId(String scenarioId) {
		ScenarioId = scenarioId;
	}

	public String getScenarioName() {
		return ScenarioName;
	}

	public void setScenarioName(String scenarioName) {
		ScenarioName = scenarioName;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "UserScenario [Id=" + Id + ", username=" + username + ", ScenarioId=" + ScenarioId + ", ScenarioName="
				+ ScenarioName + ", Status=" + Status + "]";
	}

}

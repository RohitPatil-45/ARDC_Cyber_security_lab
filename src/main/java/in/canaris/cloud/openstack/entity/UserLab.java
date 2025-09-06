package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_lab")
public class UserLab {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lab_id", nullable = false, updatable = false)
	private Long labId;

	@Column(name = "template_name", nullable = false, length = 255)
	private String templateName;

	@Column(name = "instance_name", nullable = false, length = 255)
	private String instanceName;

	@Column(name = "remote_type", nullable = false, length = 255)
	private String remoteType;

	@Column(name = "vnc_port", nullable = false)
	private Integer vncPort;

	@Column(name = "no_vnc_port", nullable = false)
	private Integer noVncPort;

	@Column(name = "guacamole_id", nullable = false)
	private Integer guacamoleId;

	@Column(name = "username", nullable = false, length = 255)
	private String username;

	@Column(name = "password", length = 50)
	private String password;

	@Column(name = "scenario_id")
	private Integer scenarioId;

	@Column(name = "ip_address", length = 50)
	private String ipAddress;

	@Column(name = "status", length = 50)
	private String Status;

	@Column(name = "vmstate", length = 50)
	private String VmState;

	public Long getLabId() {
		return labId;
	}

	public void setLabId(Long labId) {
		this.labId = labId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getRemoteType() {
		return remoteType;
	}

	public void setRemoteType(String remoteType) {
		this.remoteType = remoteType;
	}

	public Integer getVncPort() {
		return vncPort;
	}

	public void setVncPort(Integer vncPort) {
		this.vncPort = vncPort;
	}

	public Integer getNoVncPort() {
		return noVncPort;
	}

	public void setNoVncPort(Integer noVncPort) {
		this.noVncPort = noVncPort;
	}

	public Integer getGuacamoleId() {
		return guacamoleId;
	}

	public void setGuacamoleId(Integer guacamoleId) {
		this.guacamoleId = guacamoleId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Integer scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getVmState() {
		return VmState;
	}

	public void setVmState(String vmState) {
		VmState = vmState;
	}

	@Override
	public String toString() {
		return "UserLab [labId=" + labId + ", templateName=" + templateName + ", instanceName=" + instanceName
				+ ", remoteType=" + remoteType + ", vncPort=" + vncPort + ", noVncPort=" + noVncPort + ", guacamoleId="
				+ guacamoleId + ", username=" + username + ", password=" + password + ", scenarioId=" + scenarioId
				+ ", ipAddress=" + ipAddress + ", Status=" + Status + ", VmState=" + VmState + "]";
	}

	

}
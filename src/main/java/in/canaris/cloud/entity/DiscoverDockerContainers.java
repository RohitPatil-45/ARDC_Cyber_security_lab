package in.canaris.cloud.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "discover_docker_container")
public class DiscoverDockerContainers {
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "container_id", nullable = false, unique = true, length = 64)
    private String containerId;

    @Column(name = "container_name")
    private String containerName;

    @Column(name = "image_name", nullable = false)
    private String imageName;

    @Column(name = "command")
    private String command;

    @Column(name = "status")
    private String status;

    @Column(name = "state")
    private String state;

    @Column(name = "created")
    private Timestamp created;

    @Column(name = "physical_server_ip")
    private String physicalServerIp;

    @Column(name = "ports", columnDefinition = "TEXT")
    private String ports;

    @Column(name = "services", columnDefinition = "TEXT")
    private String services;

    @Column(name = "last_updated", insertable = false, updatable = false)
    private Timestamp lastUpdated;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public String getPhysicalServerIp() {
		return physicalServerIp;
	}

	public void setPhysicalServerIp(String physicalServerIp) {
		this.physicalServerIp = physicalServerIp;
	}

	public String getPorts() {
		return ports;
	}

	public void setPorts(String ports) {
		this.ports = ports;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public Timestamp getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
    

}

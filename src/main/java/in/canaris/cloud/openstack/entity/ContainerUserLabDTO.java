package in.canaris.cloud.openstack.entity;

import java.sql.Timestamp;

public class ContainerUserLabDTO {
	
	  private int id;
	    private String containerId;
	    private String imageName;
	    private String command;
	    private Timestamp created;
	    private String status;
	    private String ports;
	    private String containerName;
	    private String physicalServerIp;

	    private String username;
	    private String scenarioName;
	    private Timestamp lastActiveConnection;
	    
	    private String guacamoleId;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getContainerId() {
			return containerId;
		}
		public void setContainerId(String containerId) {
			this.containerId = containerId;
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
		public Timestamp getCreated() {
			return created;
		}
		public void setCreated(Timestamp created) {
			this.created = created;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getPorts() {
			return ports;
		}
		public void setPorts(String ports) {
			this.ports = ports;
		}
		public String getContainerName() {
			return containerName;
		}
		public void setContainerName(String containerName) {
			this.containerName = containerName;
		}
		public String getPhysicalServerIp() {
			return physicalServerIp;
		}
		public void setPhysicalServerIp(String physicalServerIp) {
			this.physicalServerIp = physicalServerIp;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getScenarioName() {
			return scenarioName;
		}
		public void setScenarioName(String scenarioName) {
			this.scenarioName = scenarioName;
		}
		public Timestamp getLastActiveConnection() {
			return lastActiveConnection;
		}
		public void setLastActiveConnection(Timestamp lastActiveConnection) {
			this.lastActiveConnection = lastActiveConnection;
		}
		public String getGuacamoleId() {
			return guacamoleId;
		}
		public void setGuacamoleId(String guacamoleId) {
			this.guacamoleId = guacamoleId;
		}
	    
	    

}

package in.canaris.cloud.openstack.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "discover_container")
public class DiscoverContainer {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", updatable = false, nullable = false)
    private int id;

    @Column(name = "container_id", nullable = false, length = 255)
    private String containerId; 

    @Column(name = "image_name", nullable = false, length = 255)
    private String imageName;

    @Column(name = "command", length = 255)
    private String command;

    @Column(name = "created")
    private Timestamp created;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "ports", length = 255)
    private String ports;

    @Column(name = "container_name", length = 255)
    private String containerName;

    @Column(name = "physical_server_ip", length = 255)
    private String physicalServerIp;

   

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
    
}

package in.canaris.cloud.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cloud_instance_node_health_history")
public class CloudInstanceNodeHealthHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "CPU_UTILIZATION")
    private Double cpuUtilization;

    @Column(name = "EVENT_TIMESTAMP")
    private Timestamp eventTimestamp;

    @Column(name = "MEMORY_UTILIZATION")
    private Double memoryUtilization;

    @Column(name = "NODE_IP")
    private String nodeIp;

    @Column(name = "TEMPERATURE")
    private Double temperature;

    @Column(name = "TOTAL_MEMORY")
    private Double totalMemory;

    @Column(name = "USED_MEMORY")
    private Double usedMemory;

    @Column(name = "FREE_MEMORY")
    private Double freeMemory;
    
    
    @Column(name = "TOTAL_DISK")
    private Double totalDisk;

    @Column(name = "USED_DISK")
    private Double usedDisk;

    @Column(name = "FREE_DISK")
    private Double freeDisk;


    @Column(name = "VM_NAME")
    private String vmName;
    
    
    // Getters and Setters

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Double getCpuUtilization() {
		return cpuUtilization;
	}


	public void setCpuUtilization(Double cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}


	public Timestamp getEventTimestamp() {
		return eventTimestamp;
	}


	public void setEventTimestamp(Timestamp eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}


	public Double getMemoryUtilization() {
		return memoryUtilization;
	}


	public void setMemoryUtilization(Double memoryUtilization) {
		this.memoryUtilization = memoryUtilization;
	}


	public String getNodeIp() {
		return nodeIp;
	}


	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}


	public Double getTemperature() {
		return temperature;
	}


	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}


	public Double getTotalMemory() {
		return totalMemory;
	}


	public void setTotalMemory(Double totalMemory) {
		this.totalMemory = totalMemory;
	}


	public Double getUsedMemory() {
		return usedMemory;
	}


	public void setUsedMemory(Double usedMemory) {
		this.usedMemory = usedMemory;
	}


	public Double getFreeMemory() {
		return freeMemory;
	}


	public void setFreeMemory(Double freeMemory) {
		this.freeMemory = freeMemory;
	}


	public Double getTotalDisk() {
		return totalDisk;
	}


	public void setTotalDisk(Double totalDisk) {
		this.totalDisk = totalDisk;
	}


	public Double getUsedDisk() {
		return usedDisk;
	}


	public void setUsedDisk(Double usedDisk) {
		this.usedDisk = usedDisk;
	}


	public Double getFreeDisk() {
		return freeDisk;
	}


	public void setFreeDisk(Double freeDisk) {
		this.freeDisk = freeDisk;
	}


	public String getVmName() {
		return vmName;
	}


	public void setVmName(String vmName) {
		this.vmName = vmName;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	@Override
	public String toString() {
		return "CloudInstanceNodeHealthHistory [id=" + id + ", cpuUtilization=" + cpuUtilization + ", eventTimestamp="
				+ eventTimestamp + ", memoryUtilization=" + memoryUtilization + ", nodeIp=" + nodeIp + ", temperature="
				+ temperature + ", totalMemory=" + totalMemory + ", usedMemory=" + usedMemory + ", freeMemory="
				+ freeMemory + ", totalDisk=" + totalDisk + ", usedDisk=" + usedDisk + ", freeDisk=" + freeDisk
				+ ", vmName=" + vmName + "]";
	}
  
    
}

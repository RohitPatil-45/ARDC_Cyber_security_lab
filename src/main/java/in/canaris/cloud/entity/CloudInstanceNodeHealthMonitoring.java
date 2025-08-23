package in.canaris.cloud.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cloud_instance_node_health_monitoring")
public class CloudInstanceNodeHealthMonitoring implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "CPU_STATUS")
    private String cpuStatus;

    @Column(name = "CPU_UTILIZATION")
    private Integer cpuUtilization;

    @Column(name = "FREE_MEMORY")
    private Double freeMemory;

    @Column(name = "MAKE_AND_MODEL")
    private String makeAndModel;

    @Column(name = "MEMORY_STATUS")
    private String memoryStatus;

    @Column(name = "MEMORY_UTILIZATION")
    private Integer memoryUtilization;

    @Column(name = "DISK_UTILIZATION")
    private Integer diskUtilization;

    @Column(name = "NODE_IP")
    private String nodeIp;

    @Column(name = "NODE_NAME")
    private String nodeName;

    @Column(name = "SERIAL_NO")
    private String serialNo;

    @Column(name = "TEMPERATURE")
    private Integer temperature;

    @Column(name = "TOTAL_MEMORY")
    private Double totalMemory;

    @Column(name = "UPTIME")
    private String uptime;

    @Column(name = "USED_MEMORY")
    private Double usedMemory;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "VM_NAME")
    private String vmName;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpuStatus() {
        return cpuStatus;
    }

    public void setCpuStatus(String cpuStatus) {
        this.cpuStatus = cpuStatus;
    }

    public Integer getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(Integer cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    public Double getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Double freeMemory) {
        this.freeMemory = freeMemory;
    }

    public String getMakeAndModel() {
        return makeAndModel;
    }

    public void setMakeAndModel(String makeAndModel) {
        this.makeAndModel = makeAndModel;
    }

    public String getMemoryStatus() {
        return memoryStatus;
    }

    public void setMemoryStatus(String memoryStatus) {
        this.memoryStatus = memoryStatus;
    }

    public Integer getMemoryUtilization() {
        return memoryUtilization;
    }

    public void setMemoryUtilization(Integer memoryUtilization) {
        this.memoryUtilization = memoryUtilization;
    }

    public Integer getDiskUtilization() {
        return diskUtilization;
    }

    public void setDiskUtilization(Integer diskUtilization) {
        this.diskUtilization = diskUtilization;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Double getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(Double totalMemory) {
        this.totalMemory = totalMemory;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public Double getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(Double usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

	
}

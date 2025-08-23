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
@Table(name = "cloud_instance_cpu_threshold_history")
public class CloudInstanceCpuThresholdHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "CPU_SVMStatusHistoryTATUS")
    private String cpuStatus;

    @Column(name = "CPU_THRESHOLD")
    private Double cpuThreshold;

    @Column(name = "CPU_UTILIZATION")
    private Double cpuUtilization;

    @Column(name = "EVENT_TIMESTAMP")
    private Timestamp eventTimestamp;

    @Column(name = "NODE_IP")
    private String nodeIp;

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

    public Double getCpuThreshold() {
        return cpuThreshold;
    }

    public void setCpuThreshold(Double cpuThreshold) {
        this.cpuThreshold = cpuThreshold;
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

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }
}

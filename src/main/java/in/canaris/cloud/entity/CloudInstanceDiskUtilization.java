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
@Table(name = "cloud_instance_disk_utilization")
public class CloudInstanceDiskUtilization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "DEVICE_IP")
    private String deviceIp;

    @Column(name = "VM_NAME")
    private String vmName;

    @Column(name = "EVENT_TIME")
    private Timestamp eventTime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DISK_UTILIZATION")
    private Integer diskUtilization;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDiskUtilization() {
        return diskUtilization;
    }

    public void setDiskUtilization(Integer diskUtilization) {
        this.diskUtilization = diskUtilization;
    }
}

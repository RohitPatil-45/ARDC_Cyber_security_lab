package in.canaris.cloud.server.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "discover_drives")
public class DiscoverDrives implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SR_NO", updatable = false, nullable = false)
	private long id;
	
	@Column(name="DEVICE_NAME")
	private String deviceName;
	
	@Column(name="DEVICE_IP")
	private String deviceIP;
	
	@Column(name="DRIVE_NAME")
	private String driveName;
	
	@Column(name="TOTAL_SPACE")
	private String totalSpace;
	
	@Column(name="USED_SPACE")
	private String usedSpace;
	
	@Column(name="FREE_SPACE")
	private String freeSpace;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="DRIVE_STATUS")
	private String driveStatus;
	
	@Column(name="DRIVE_UTILIZATION_PERCENTAGE")
	private String driveUtilizationPercentage;
	
	@Column(name="EVENT_TIME")
	private Timestamp eventTime;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceIP() {
		return deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	public String getDriveName() {
		return driveName;
	}

	public void setDriveName(String driveName) {
		this.driveName = driveName;
	}

	public String getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(String totalSpace) {
		this.totalSpace = totalSpace;
	}

	public String getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(String usedSpace) {
		this.usedSpace = usedSpace;
	}

	public String getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(String freeSpace) {
		this.freeSpace = freeSpace;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDriveStatus() {
		return driveStatus;
	}

	public void setDriveStatus(String driveStatus) {
		this.driveStatus = driveStatus;
	}

	public String getDriveUtilizationPercentage() {
		return driveUtilizationPercentage;
	}

	public void setDriveUtilizationPercentage(String driveUtilizationPercentage) {
		this.driveUtilizationPercentage = driveUtilizationPercentage;
	}

	public Timestamp getEventTime() {
		return eventTime;
	}

	public void setEventTime(Timestamp eventTime) {
		this.eventTime = eventTime;
	}
	
}

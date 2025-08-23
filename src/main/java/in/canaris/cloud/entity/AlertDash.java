package in.canaris.cloud.entity;

import java.sql.Timestamp;

public class AlertDash {

	private String Alert_Name;
	private String VM_Name;
	private String physicalServer_ip;
	private double MEMORY_UTILIZATION;
	private double MEMORY_THRESHOLD;
	private double Difference;
	private String Alert_STATUS;
	private Timestamp EVENT_TIMESTAMP;

	public String getPhysicalServer_ip() {
		return physicalServer_ip;
	}

	public void setPhysicalServer_ip(String physicalServer_ip) {
		this.physicalServer_ip = physicalServer_ip;
	}

	public String getAlert_Name() {
		return Alert_Name;
	}

	public void setAlert_Name(String alert_Name) {
		Alert_Name = alert_Name;
	}

	public String getVM_Name() {
		return VM_Name;
	}

	public void setVM_Name(String vM_Name) {
		VM_Name = vM_Name;
	}

	public double getMEMORY_UTILIZATION() {
		return MEMORY_UTILIZATION;
	}

	public void setMEMORY_UTILIZATION(double mEMORY_UTILIZATION) {
		MEMORY_UTILIZATION = mEMORY_UTILIZATION;
	}

	public double getMEMORY_THRESHOLD() {
		return MEMORY_THRESHOLD;
	}

	public void setMEMORY_THRESHOLD(double mEMORY_THRESHOLD) {
		MEMORY_THRESHOLD = mEMORY_THRESHOLD;
	}

	public double getDifference() {
		return Difference;
	}

	public void setDifference(double difference) {
		Difference = difference;
	}

	public String getAlert_STATUS() {
		return Alert_STATUS;
	}

	public void setAlert_STATUS(String alert_STATUS) {
		Alert_STATUS = alert_STATUS;
	}

	public Timestamp getEVENT_TIMESTAMP() {
		return EVENT_TIMESTAMP;
	}

	public void setEVENT_TIMESTAMP(Timestamp eVENT_TIMESTAMP) {
		EVENT_TIMESTAMP = eVENT_TIMESTAMP;
	}

}

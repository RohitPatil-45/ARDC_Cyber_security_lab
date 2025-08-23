package in.canaris.cloud.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "kvm_drive_historical_log")
public class kvm_drive_historical_log {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column
	private String device;
	@Column
	private String used;
	@Column
	private String capacity;
	@Column
	private String bus;
	@Column
	private String access;
	@Column
	private String source;
	@Column
	private String physicalserverip;
	@Column
	private String vmname;
	@Column
	private Timestamp EventTimestamp;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getUsed() {
		return used;
	}
	public void setUsed(String used) {
		this.used = used;
	}
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public String getBus() {
		return bus;
	}
	public void setBus(String bus) {
		this.bus = bus;
	}
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPhysicalserverip() {
		return physicalserverip;
	}
	public void setPhysicalserverip(String physicalserverip) {
		this.physicalserverip = physicalserverip;
	}
	public String getVmname() {
		return vmname;
	}
	public void setVmname(String vmname) {
		this.vmname = vmname;
	}
	public Timestamp getEventTimestamp() {
		return EventTimestamp;
	}
	public void setEventTimestamp(Timestamp eventTimestamp) {
		EventTimestamp = eventTimestamp;
	}
	
	@Override
	public String toString() {
		return "kvm_drive_historical_log [id=" + id + ", device=" + device + ", used=" + used + ", capacity=" + capacity
				+ ", bus=" + bus + ", access=" + access + ", source=" + source + ", physicalserverip="
				+ physicalserverip + ", vmname=" + vmname + ", EventTimestamp=" + EventTimestamp + "]";
	}
	
	
	
	
	

}

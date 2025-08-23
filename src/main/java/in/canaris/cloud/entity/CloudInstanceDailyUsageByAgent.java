package in.canaris.cloud.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cloud_instance_dailyusage_byagent")
public class CloudInstanceDailyUsageByAgent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column(name = "Event_Date")
	private Date eventDate;

	@Column(name = "Total_Duration", nullable = false)
	private long totalDuration;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "instance_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private CloudInstance instance_id;

	@Column(name = "TIMEinHMS")
	private String timeInHMS;
	
	@Column(name = "Vm_Name")
	private String vmname;

	// Getters and Setters

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public long getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(long totalDuration) {
		this.totalDuration = totalDuration;
	}

	public CloudInstance getInstance_id() {
		return instance_id;
	}

	public void setInstance_id(CloudInstance instance_id) {
		this.instance_id = instance_id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTimeInHMS() {
		return timeInHMS;
	}

	public void setTimeInHMS(String timeInHMS) {
		this.timeInHMS = timeInHMS;
	}

	public String getVmname() {
		return vmname;
	}

	public void setVmname(String vmname) {
		this.vmname = vmname;
	}
	
	
}

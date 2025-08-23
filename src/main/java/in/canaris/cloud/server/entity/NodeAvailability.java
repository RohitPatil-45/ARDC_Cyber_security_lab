package in.canaris.cloud.server.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "NODE_AVAILABILITY")
public class NodeAvailability implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long ID;
	
	@Column(name="NODE_IP")
	private String nodeIP;
	
	@Column(name="UPTIME_PERCENT")
	private double uptimePercent;
	
	@Column(name="UPTIME_MILISECONDS")
	private int uptimeMiliseconds;
	
	@Column(name="UPTIME_STR")
	private String uptimeStr;
	
	@Column(name="DOWNTIME_MILISECONDS")
	private int downtimeMiliseconds;
	
	@Column(name="DOWNTIME_PERCENT")
	private int downtimePercent;
	
	@Column(name="DOWNTIME_STR")
	private String downtimeStr;
	
	@Column(name="EVENT_TIMESTAMP")
	private Date eventTime;

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public String getNodeIP() {
		return nodeIP;
	}

	public void setNodeIP(String nodeIP) {
		this.nodeIP = nodeIP;
	}

	public double getUptimePercent() {
		return uptimePercent;
	}

	public void setUptimePercent(double uptimePercent) {
		this.uptimePercent = uptimePercent;
	}

	public int getUptimeMiliseconds() {
		return uptimeMiliseconds;
	}

	public void setUptimeMiliseconds(int uptimeMiliseconds) {
		this.uptimeMiliseconds = uptimeMiliseconds;
	}

	public int getDowntimePercent() {
		return downtimePercent;
	}

	public void setDowntimePercent(int downtimePercent) {
		this.downtimePercent = downtimePercent;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public int getDowntimeMiliseconds() {
		return downtimeMiliseconds;
	}

	public void setDowntimeMiliseconds(int downtimeMiliseconds) {
		this.downtimeMiliseconds = downtimeMiliseconds;
	}

	public String getUptimeStr() {
		return uptimeStr;
	}

	public void setUptimeStr(String uptimeStr) {
		this.uptimeStr = uptimeStr;
	}

	public String getDowntimeStr() {
		return downtimeStr;
	}

	public void setDowntimeStr(String downtimeStr) {
		this.downtimeStr = downtimeStr;
	}

}

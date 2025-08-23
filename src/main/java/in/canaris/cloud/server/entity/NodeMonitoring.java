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
@Table(name = "NODE_MONITORING")
public class NodeMonitoring implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private Long ID;
	
	@Column(name="NODE_IP")
	private String nodeIP;
	
	@Column(name="NODE_STATUS")
	private String nodeStatus;
	
	@Column(name="LATENCY")
	private Float latency;
	
	@Column(name="LATENCY_STATUS")
	private String latencyStatus;
	
	@Column(name="LATENCY_THRESHOLD")
	private String latencyThreshold;
	
	@Column(name="LATENCY_TIMESTAMP")
	private Timestamp latencyTimestamp;
	
	@Column(name="PACKET_LOSS")
	private Float packetLoss;
	
	@Column(name="MAX_RESPONSE")
	private int maxResponse;
	
	@Column(name="MIN_RESPONSE")
	private int minResponse;
	
	@Column(name="STATUS_TIMESTAMP")
	private Timestamp statusTimestamp;

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

	public String getNodeStatus() {
		return nodeStatus;
	}

	public void setNodeStatus(String nodeStatus) {
		this.nodeStatus = nodeStatus;
	}

	public Float getLatency() {
		return latency;
	}

	public void setLatency(Float latency) {
		this.latency = latency;
	}

	public String getLatencyStatus() {
		return latencyStatus;
	}

	public void setLatencyStatus(String latencyStatus) {
		this.latencyStatus = latencyStatus;
	}

	public String getLatencyThreshold() {
		return latencyThreshold;
	}

	public void setLatencyThreshold(String latencyThreshold) {
		this.latencyThreshold = latencyThreshold;
	}

	public Timestamp getLatencyTimestamp() {
		return latencyTimestamp;
	}

	public void setLatencyTimestamp(Timestamp latencyTimestamp) {
		this.latencyTimestamp = latencyTimestamp;
	}

	public Float getPacketLoss() {
		return packetLoss;
	}

	public void setPacketLoss(Float packetLoss) {
		this.packetLoss = packetLoss;
	}

	public int getMaxResponse() {
		return maxResponse;
	}

	public void setMaxResponse(int maxResponse) {
		this.maxResponse = maxResponse;
	}

	public int getMinResponse() {
		return minResponse;
	}

	public void setMinResponse(int minResponse) {
		this.minResponse = minResponse;
	}

	public Timestamp getStatusTimestamp() {
		return statusTimestamp;
	}

	public void setStatusTimestamp(Timestamp statusTimestamp) {
		this.statusTimestamp = statusTimestamp;
	}

}

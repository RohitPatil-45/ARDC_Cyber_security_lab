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
@Table(name = "interface_monitoring")
public class InterfaceMonitoring implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private Long ID;
	
	@Column(name="NODE_IP")
	private String nodeIP;
	
	@Column(name="INTERFACE_NAME")
	private String interfaceName;
	
	@Column(name="INTERFACE_ID")
	private String interfaceID;
	
	@Column(name="INTERFACE_TYPE")
	private String interfaceType;
	
	@Column(name="ADMIN_STATUS")
	private String adminStatus;
	
	@Column(name="OPER_STATUS")
	private String operStatus;
	
	@Column(name="PROCURED_BANDWIDTH")
	private double procuredBandwidth;
	
	@Column(name="INTERFACE_MACADDRESS")
	private String interfaceMacAddress;
	
	@Column(name="INTERFACE_IP")
	private String interfaceIP;
	
	@Column(name="ALIAS_NAME")
	private String aliasName;
	
	@Column(name="CRC_ERROR")
	private String crcError;
	
	@Column(name="INTERFACE_ERROR")
	private String interfaceError;
	
	@Column(name="MTU")
	private String mtu;
	
	@Column(name="OUT_TRAFFIC")
	private double outTraffic;
	
	@Column(name="IN_TRAFFIC")
	private double inTraffic;
	
	@Column(name="DISCARD_INPUT")
	private String discardInput;

	@Column(name="DISCARD_OUTPUT")
	private String discardOutput;
	
	@Column(name="INTERFACE_INPUT_ERROR")
	private String interfaceInputError;
	
	@Column(name="INTERFACE_OUTPUT_ERROR")
	private String interfaceOutputError;
	
	@Column(name="MONITORING_PARAM")
	private String monitoringParam;
	
	@Column(name="MAIL_ALERT")
	private String mailAlert;
	
	@Column(name="SMS_ALERT")
	private String smsAlert;
	
	@Column(name="AUTO_TICKETING")
	private String autoTicketing;
	
	@Column(name="BW_THRESHOLD")
	private double bwThreshold;
	
	@Column(name="BW_HISTORY_PARAM")
	private String bwHistoryParam;
	
	@Column(name="CRC_HISTORY_PARAM")
	private String crcHistoryParam;
	
	@Column(name="STATUS_TIMESTAMP")
	private Timestamp statusTimestamp;
	
	@Column(name="OUT_BW_PERCENT")
	private double outBwPercent;
	
	@Column(name="IN_BW_PERCENT")
	private double inBwPercent;
	
	@Column(name="BW_STATUS")
	private String bwStatus;
	
	@Column(name="BW_TYPE")
	private String bwType;
	
	@Column(name="BW_TIMESTAMP")
	private Timestamp bwTimestamp;

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

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getInterfaceID() {
		return interfaceID;
	}

	public void setInterfaceID(String interfaceID) {
		this.interfaceID = interfaceID;
	}

	public String getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	public String getAdminStatus() {
		return adminStatus;
	}

	public void setAdminStatus(String adminStatus) {
		this.adminStatus = adminStatus;
	}

	public String getOperStatus() {
		return operStatus;
	}

	public void setOperStatus(String operStatus) {
		this.operStatus = operStatus;
	}

	public double getProcuredBandwidth() {
		return procuredBandwidth;
	}

	public void setProcuredBandwidth(double procuredBandwidth) {
		this.procuredBandwidth = procuredBandwidth;
	}

	public String getInterfaceMacAddress() {
		return interfaceMacAddress;
	}

	public void setInterfaceMacAddress(String interfaceMacAddress) {
		this.interfaceMacAddress = interfaceMacAddress;
	}

	public String getInterfaceIP() {
		return interfaceIP;
	}

	public void setInterfaceIP(String interfaceIP) {
		this.interfaceIP = interfaceIP;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getCrcError() {
		return crcError;
	}

	public void setCrcError(String crcError) {
		this.crcError = crcError;
	}

	public String getInterfaceError() {
		return interfaceError;
	}

	public void setInterfaceError(String interfaceError) {
		this.interfaceError = interfaceError;
	}

	public String getMtu() {
		return mtu;
	}

	public void setMtu(String mtu) {
		this.mtu = mtu;
	}

	public double getOutTraffic() {
		return outTraffic;
	}

	public void setOutTraffic(double outTraffic) {
		this.outTraffic = outTraffic;
	}

	public double getInTraffic() {
		return inTraffic;
	}

	public void setInTraffic(double inTraffic) {
		this.inTraffic = inTraffic;
	}

	public String getDiscardInput() {
		return discardInput;
	}

	public void setDiscardInput(String discardInput) {
		this.discardInput = discardInput;
	}

	public String getDiscardOutput() {
		return discardOutput;
	}

	public void setDiscardOutput(String discardOutput) {
		this.discardOutput = discardOutput;
	}

	public String getInterfaceInputError() {
		return interfaceInputError;
	}

	public void setInterfaceInputError(String interfaceInputError) {
		this.interfaceInputError = interfaceInputError;
	}

	public String getInterfaceOutputError() {
		return interfaceOutputError;
	}

	public void setInterfaceOutputError(String interfaceOutputError) {
		this.interfaceOutputError = interfaceOutputError;
	}

	public String getMonitoringParam() {
		return monitoringParam;
	}

	public void setMonitoringParam(String monitoringParam) {
		this.monitoringParam = monitoringParam;
	}

	public String getMailAlert() {
		return mailAlert;
	}

	public void setMailAlert(String mailAlert) {
		this.mailAlert = mailAlert;
	}

	public String getSmsAlert() {
		return smsAlert;
	}

	public void setSmsAlert(String smsAlert) {
		this.smsAlert = smsAlert;
	}

	public String getAutoTicketing() {
		return autoTicketing;
	}

	public void setAutoTicketing(String autoTicketing) {
		this.autoTicketing = autoTicketing;
	}

	public double getBwThreshold() {
		return bwThreshold;
	}

	public void setBwThreshold(double bwThreshold) {
		this.bwThreshold = bwThreshold;
	}

	public String getBwHistoryParam() {
		return bwHistoryParam;
	}

	public void setBwHistoryParam(String bwHistoryParam) {
		this.bwHistoryParam = bwHistoryParam;
	}

	public String getCrcHistoryParam() {
		return crcHistoryParam;
	}

	public void setCrcHistoryParam(String crcHistoryParam) {
		this.crcHistoryParam = crcHistoryParam;
	}

	public Timestamp getStatusTimestamp() {
		return statusTimestamp;
	}

	public void setStatusTimestamp(Timestamp statusTimestamp) {
		this.statusTimestamp = statusTimestamp;
	}

	public double getOutBwPercent() {
		return outBwPercent;
	}

	public void setOutBwPercent(double outBwPercent) {
		this.outBwPercent = outBwPercent;
	}

	public double getInBwPercent() {
		return inBwPercent;
	}

	public void setInBwPercent(double inBwPercent) {
		this.inBwPercent = inBwPercent;
	}

	public String getBwStatus() {
		return bwStatus;
	}

	public void setBwStatus(String bwStatus) {
		this.bwStatus = bwStatus;
	}

	public String getBwType() {
		return bwType;
	}

	public void setBwType(String bwType) {
		this.bwType = bwType;
	}

	public Timestamp getBwTimestamp() {
		return bwTimestamp;
	}

	public void setBwTimestamp(Timestamp bwTimestamp) {
		this.bwTimestamp = bwTimestamp;
	}
	
	

}

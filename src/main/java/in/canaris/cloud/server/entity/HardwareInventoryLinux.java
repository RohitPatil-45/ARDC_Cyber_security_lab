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
@Table(name = "hardware_inventory_linux")
public class HardwareInventoryLinux implements Serializable{
	
	private static final long serialVersionUID = -7566152671795586625L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SR_NO", updatable = false, nullable = false)
    private Long srNo;

    @Column(name = "IP_ADDRESS", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String ipAddress;

    @Column(name = "PC_NAME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String pcName;

    @Column(name = "BRANCH_NAME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String branchName;

    @Column(name = "BIOS_INFO", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String biosInfo;

    @Column(name = "GRAPHIC_CARD", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String graphicCard;

    @Column(name = "HDD_DRIVE", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String hddDrive;

    @Column(name = "MAC_ADDRESS", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String macAddress;

    @Column(name = "MOTHERBOARD_NAME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String motherboardName;

    @Column(name = "OP_NAME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String opName;

    @Column(name = "PROCESSOR_NAME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String processorName;

    @Column(name = "RAM_DETAILS", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String ramDetails;

    @Column(name = "SERIAL_NO", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String serialNo;

    @Column(name = "DISCOVER_TIME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String discoverTime;

    @Column(name = "CTIME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String cTime;

    @Column(name = "HOSTNAME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String hostname;

    @Column(name = "OS_NAME", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String osName;

    @Column(name = "VERSION", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String version;

    @Column(name = "ARCHITECTURE", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String architecture;

	public Long getSrNo() {
		return srNo;
	}

	public void setSrNo(Long srNo) {
		this.srNo = srNo;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPcName() {
		return pcName;
	}

	public void setPcName(String pcName) {
		this.pcName = pcName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBiosInfo() {
		return biosInfo;
	}

	public void setBiosInfo(String biosInfo) {
		this.biosInfo = biosInfo;
	}

	public String getGraphicCard() {
		return graphicCard;
	}

	public void setGraphicCard(String graphicCard) {
		this.graphicCard = graphicCard;
	}

	public String getHddDrive() {
		return hddDrive;
	}

	public void setHddDrive(String hddDrive) {
		this.hddDrive = hddDrive;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getMotherboardName() {
		return motherboardName;
	}

	public void setMotherboardName(String motherboardName) {
		this.motherboardName = motherboardName;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	public String getRamDetails() {
		return ramDetails;
	}

	public void setRamDetails(String ramDetails) {
		this.ramDetails = ramDetails;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getDiscoverTime() {
		return discoverTime;
	}

	public void setDiscoverTime(String discoverTime) {
		this.discoverTime = discoverTime;
	}

	public String getcTime() {
		return cTime;
	}

	public void setcTime(String cTime) {
		this.cTime = cTime;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	
	
}

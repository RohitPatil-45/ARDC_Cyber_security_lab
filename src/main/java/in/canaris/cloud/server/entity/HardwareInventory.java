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
@Table(name = "Hardware_Inventory")
public class HardwareInventory implements Serializable{
	
	private static final long serialVersionUID = -7566152671795586625L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "bios_manufacturer", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String biosManufacturer;

    @Column(name = "bios_name", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String biosName;

    @Column(name = "bios_release_date", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String biosReleaseDate;

    @Column(name = "bios_version", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String biosVersion;

    @Column(name = "build_number", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String buildNumber;

    @Column(name = "disk_space", nullable = false, columnDefinition = "double default 0")
    private Double diskSpace;

    @Column(name = "domain", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String domain;

    @Column(name = "host_name", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String hostName;

    @Column(name = "ip_address", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String ipAddress;

    @Column(name = "mac_address", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String macAddress;

    @Column(name = "manufacturer", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String manufacturer;

    @Column(name = "original_serial_no", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String originalSerialNo;

    @Column(name = "os_name", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String osName;

    @Column(name = "os_type", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String osType;

    @Column(name = "os_version", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String osVersion;

    @Column(name = "processor_count", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String processorCount;

    @Column(name = "processor_manufacturer", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String processorManufacturer;

    @Column(name = "processor_name", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String processorName;

    @Column(name = "product_id", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String productId;

    @Column(name = "service_pack", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String servicePack;

    @Column(name = "system_model", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String systemModel;

    @Column(name = "total_memory", nullable = false, columnDefinition = "double default 0")
    private Double totalMemory;
    
    @Column(name = "device_ip", length = 191, nullable = false, columnDefinition = "varchar(191) default '-'")
    private String deviceIP;    
    @Column
    private String device_name;
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBiosManufacturer() {
		return biosManufacturer;
	}

	public void setBiosManufacturer(String biosManufacturer) {
		this.biosManufacturer = biosManufacturer;
	}

	public String getBiosName() {
		return biosName;
	}

	public void setBiosName(String biosName) {
		this.biosName = biosName;
	}

	public String getBiosReleaseDate() {
		return biosReleaseDate;
	}

	public void setBiosReleaseDate(String biosReleaseDate) {
		this.biosReleaseDate = biosReleaseDate;
	}

	public String getBiosVersion() {
		return biosVersion;
	}

	public void setBiosVersion(String biosVersion) {
		this.biosVersion = biosVersion;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	public Double getDiskSpace() {
		return diskSpace;
	}

	public void setDiskSpace(Double diskSpace) {
		this.diskSpace = diskSpace;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getOriginalSerialNo() {
		return originalSerialNo;
	}

	public void setOriginalSerialNo(String originalSerialNo) {
		this.originalSerialNo = originalSerialNo;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getProcessorCount() {
		return processorCount;
	}

	public void setProcessorCount(String processorCount) {
		this.processorCount = processorCount;
	}

	public String getProcessorManufacturer() {
		return processorManufacturer;
	}

	public void setProcessorManufacturer(String processorManufacturer) {
		this.processorManufacturer = processorManufacturer;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getServicePack() {
		return servicePack;
	}

	public void setServicePack(String servicePack) {
		this.servicePack = servicePack;
	}

	public String getSystemModel() {
		return systemModel;
	}

	public void setSystemModel(String systemModel) {
		this.systemModel = systemModel;
	}

	public Double getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(Double totalMemory) {
		this.totalMemory = totalMemory;
	}
	
	
	public String getDeviceIP() {
		return deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}
	
}

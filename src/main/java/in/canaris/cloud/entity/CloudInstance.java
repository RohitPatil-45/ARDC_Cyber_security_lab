package in.canaris.cloud.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import antlr.collections.List;

@Entity
@Table(name = "cloud_instance")
public class CloudInstance implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column
	private String vm_start_stop_status;

	@Column
	private Timestamp vm_start_stop_time;

	@Column
	private String instance_name;

	@Column
	private String uuid;

	@Column
	private String instance_password;

	@Column
	private String instance_ip;

	@Column(name = "physical_server_ip")
	private String physicalServerIP;

	@Column
	private String generation_type;

	@Column
	private String vm_location_path;

	@Column
	private String vlan_setting;

	@Column
	private String mac_address;

	@Column
	private String computer_name;

	@Column
	private String vm_id;

	@Column
	private String vm_state;

	@Column
	private String vm_status;

	@Column
	private String request_status;

	@Column
	private String disk_path;

	@Column
	private String iso_file_path;

	@Column(name = "is_monitoring")
	private boolean isMonitoring;

	@Column
	private int size_of_system_files;

	@Column
	@CreationTimestamp
	private Timestamp created_on;

	@Column
	private String lab_id;

	@Lob
	@Column(name = "lab_image", nullable = true)
	private byte[] lab_image;

	@Column
	private String lab_tag;

	@Column
	private String vnc_port;

	@Column
	private String web_id;
	
	@Column
	private String ConsoleUsername;
	
	@Column
	private String ConsolePassword;
	
	@Column
	private String ConsoleProtocol;
	
	@Column
	private String AssignedLab;
	
	
	

	@Lob
	@Column(name = "description", nullable = true)
	private String description;

	@Lob
	@Column(name = "vm_instructions", nullable = true)
	private String vm_instructions;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "location_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Location location_id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vpc_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private VPC vpc_id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "security_group_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private SecurityGroup security_group_id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "price_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Price price_id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "switch_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Switch switch_id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "subproduct_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private SubProduct subproduct_id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "discount_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Discount discount_id;

	@Column(name = "group_name")
	private String groupName;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "memory_assigned")
	private String memoryAssigned;

	@Column(name = "memory_assignedkvm")
	private String memory_assignedkvm;

	@Column(name = "cpu_assigned")
	private String cpuAssigned;

	@Column(name = "disk_assigned")
	private String diskAssigned;

	@Column(name = "virtualization_type")
	private String virtualization_type;

	@Column(name = "kvm_os_type")
	private String kvmostype;

	@Column(name = "kvm_vm_id")
	private String kvmvmid;
	
	@Column
	private String guacamoleId;

	public String getKvmostype() {
		return kvmostype;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setKvmostype(String kvmostype) {
		this.kvmostype = kvmostype;
	}

	public String getKvmvmid() {
		return kvmvmid;
	}

	public void setKvmvmid(String kvmvmid) {
		this.kvmvmid = kvmvmid;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMemory_assignedkvm() {
		return memory_assignedkvm;
	}

	public void setMemory_assignedkvm(String memory_assignedkvm) {
		this.memory_assignedkvm = memory_assignedkvm;
	}

	public String getVirtualization_type() {
		return virtualization_type;
	}

	public void setVirtualization_type(String virtualization_type) {
		this.virtualization_type = virtualization_type;
	}

	public String getInstance_ip() {
		return instance_ip;
	}

	public void setInstance_ip(String instance_ip) {
		this.instance_ip = instance_ip;
	}

	public String getPhysicalServerIP() {
		return physicalServerIP;
	}

	public void setPhysicalServerIP(String physicalServerIP) {
		this.physicalServerIP = physicalServerIP;
	}

	public String getInstance_name() {
		return instance_name;
	}

	public void setInstance_name(String instance_name) {
		this.instance_name = instance_name;
	}

	public String getInstance_password() {
		return instance_password;
	}

	public void setInstance_password(String instance_password) {
		this.instance_password = instance_password;
	}

	public Timestamp getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Timestamp created_on) {
		this.created_on = created_on;
	}

	public Location getLocation_id() {
		return location_id;
	}

	public void setLocation_id(Location location_id) {
		this.location_id = location_id;
	}

	public VPC getVpc_id() {
		return vpc_id;
	}

	public void setVpc_id(VPC vpc_id) {
		this.vpc_id = vpc_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SecurityGroup getSecurity_group_id() {
		return security_group_id;
	}

	public void setSecurity_group_id(SecurityGroup security_group_id) {
		this.security_group_id = security_group_id;
	}

	public Price getPrice_id() {
		return price_id;
	}

	public void setPrice_id(Price price_id) {
		this.price_id = price_id;
	}

	public Switch getSwitch_id() {
		return switch_id;
	}

	public void setSwitch_id(Switch switch_id) {
		this.switch_id = switch_id;
	}

	public String getGeneration_type() {
		return generation_type;
	}

	public void setGeneration_type(String generation_type) {
		this.generation_type = generation_type;
	}

	public String getVm_location_path() {
		return vm_location_path;
	}

	public void setVm_location_path(String vm_location_path) {
		this.vm_location_path = vm_location_path;
	}

	public SubProduct getSubproduct_id() {
		return subproduct_id;
	}

	public void setSubproduct_id(SubProduct subproduct_id) {
		this.subproduct_id = subproduct_id;
	}

	public String getDisk_path() {
		return disk_path;
	}

	public void setDisk_path(String disk_path) {
		this.disk_path = disk_path;
	}

	public String getVlan_setting() {
		return vlan_setting;
	}

	public void setVlan_setting(String vlan_setting) {
		this.vlan_setting = vlan_setting;
	}

	public String getMac_address() {
		return mac_address;
	}

	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}

	public String getComputer_name() {
		return computer_name;
	}

	public void setComputer_name(String computer_name) {
		this.computer_name = computer_name;
	}

	public String getVm_id() {
		return vm_id;
	}

	public void setVm_id(String vm_id) {
		this.vm_id = vm_id;
	}

	public String getVm_state() {
		return vm_state;
	}

	public void setVm_state(String vm_state) {
		this.vm_state = vm_state;
	}

	public String getVm_status() {
		return vm_status;
	}

	public void setVm_status(String vm_status) {
		this.vm_status = vm_status;
	}

	public String getRequest_status() {
		return request_status;
	}

	public void setRequest_status(String request_status) {
		this.request_status = request_status;
	}

	public String getIso_file_path() {
		return iso_file_path;
	}

	public void setIso_file_path(String iso_file_path) {
		this.iso_file_path = iso_file_path;
	}

	public int getSize_of_system_files() {
		return size_of_system_files;
	}

	public void setSize_of_system_files(int size_of_system_files) {
		this.size_of_system_files = size_of_system_files;
	}

	public boolean isMonitoring() {
		return isMonitoring;
	}

	public void setMonitoring(boolean isMonitoring) {
		this.isMonitoring = isMonitoring;
	}

	public String getVm_start_stop_status() {
		return vm_start_stop_status;
	}

	public void setVm_start_stop_status(String vm_start_stop_status) {
		this.vm_start_stop_status = vm_start_stop_status;
	}

	public Timestamp getVm_start_stop_time() {
		return vm_start_stop_time;
	}

	public void setVm_start_stop_time(Timestamp vm_start_stop_time) {
		this.vm_start_stop_time = vm_start_stop_time;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getMemoryAssigned() {
		return memoryAssigned;
	}

	public void setMemoryAssigned(String memoryAssigned) {
		this.memoryAssigned = memoryAssigned;
	}

	public String getCpuAssigned() {
		return cpuAssigned;
	}

	public void setCpuAssigned(String cpuAssigned) {
		this.cpuAssigned = cpuAssigned;
	}

	public String getDiskAssigned() {
		return diskAssigned;
	}

	public void setDiskAssigned(String diskAssigned) {
		this.diskAssigned = diskAssigned;
	}

	public Discount getDiscount_id() {
		return discount_id;
	}

	public void setDiscount_id(Discount discount_id) {
		this.discount_id = discount_id;
	}

	public String getLab_id() {
		return lab_id;
	}

	public void setLab_id(String lab_id) {
		this.lab_id = lab_id;
	}

	public byte[] getLab_image() {
		return lab_image;
	}

	public void setLab_image(byte[] lab_image) {
		this.lab_image = lab_image;
	}

	public String getLab_tag() {
		return lab_tag;
	}

	public void setLab_tag(String lab_tag) {
		this.lab_tag = lab_tag;
	}

	public String getVnc_port() {
		return vnc_port;
	}

	public void setVnc_port(String vnc_port) {
		this.vnc_port = vnc_port;
	}

	public String getWeb_id() {
		return web_id;
	}

	public void setWeb_id(String web_id) {
		this.web_id = web_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVm_instructions() {
		return vm_instructions;
	}

	public void setVm_instructions(String vm_instructions) {
		this.vm_instructions = vm_instructions;
	}

	public String getConsoleUsername() {
		return ConsoleUsername;
	}

	public void setConsoleUsername(String consoleUsername) {
		ConsoleUsername = consoleUsername;
	}

	public String getConsolePassword() {
		return ConsolePassword;
	}

	public void setConsolePassword(String consolePassword) {
		ConsolePassword = consolePassword;
	}

	public String getConsoleProtocol() {
		return ConsoleProtocol;
	}

	public void setConsoleProtocol(String consoleProtocol) {
		ConsoleProtocol = consoleProtocol;
	}

	public String getAssignedLab() {
		return AssignedLab;
	}

	public void setAssignedLab(String assignedLab) {
		AssignedLab = assignedLab;
	}

	public String getGuacamoleId() {
		return guacamoleId;
	}

	public void setGuacamoleId(String guacamoleId) {
		this.guacamoleId = guacamoleId;
	}
	
	

}

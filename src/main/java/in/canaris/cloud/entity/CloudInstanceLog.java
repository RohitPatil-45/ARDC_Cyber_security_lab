package in.canaris.cloud.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cloud_instance_log")
public class CloudInstanceLog implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column
	private String instance_name;
	
	@Column
	private String instance_password;
	 
	@Column
	private String instance_ip;
	
	@Column
	private String generation_type;
	
	@Column
	private String vm_location_path;
	
	@Column
	private String request_type;
	
	@Column
	private String disk_path;
	
	@Column
	private String iso_file_path;
	
	@Column(name = "memory_assigned")
	private String memoryAssigned;

	@Column(name = "cpu_assigned")
	private String cpuAssigned;
	
	@Column
	@CreationTimestamp
	private Timestamp created_on;
	
	@Column
	@CreationTimestamp
	private Timestamp event_time;
	
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

	public String getInstance_ip() {
		return instance_ip;
	}

	public void setInstance_ip(String instance_ip) {
		this.instance_ip = instance_ip;
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

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	public String getIso_file_path() {
		return iso_file_path;
	}

	public void setIso_file_path(String iso_file_path) {
		this.iso_file_path = iso_file_path;
	}

	public Timestamp getEvent_time() {
		return event_time;
	}

	public void setEvent_time(Timestamp event_time) {
		this.event_time = event_time;
	}

	
}

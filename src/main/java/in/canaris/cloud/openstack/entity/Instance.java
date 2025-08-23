package in.canaris.cloud.openstack.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "openstack_instance", indexes = { @Index(name = "id", columnList = "id") })
public class Instance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Sr_no", updatable = false, nullable = false)
	private int srNo;

	@Column(name = "id", length = 255)
	private String instanceID;

	@Column(name = "name", length = 255)
	private String name;

	@Column(name = "status", length = 255)
	private String status;

	@Column(name = "tenant_id", length = 255)
	private String tenantId;

	@Column(name = "user_id", length = 255)
	private String userId;

	@Column(name = "metadata", columnDefinition = "TEXT")
	private String metadata;

	@Column(name = "host_id", length = 255)
	private String hostId;

	@Column(name = "created", length = 255)

	private String created;

	@Column(name = "updated", length = 255)

	private String updated;

	@Column(name = "accessIPv4", length = 255)
	private String accessIPv4;

	@Column(name = "accessIPv6", length = 255)
	private String accessIPv6;

	@Column(name = "disk_config", length = 255)
	private String diskConfig;

	@Column(name = "progress", length = 255)
	private String progress;

	@Column(name = "availability_zone", length = 255)
	private String availabilityZone;

	@Column(name = "config_drive", length = 255)
	private String configDrive;

	@Column(name = "key_name", length = 255)
	private String keyName;

	@Column(name = "launched_at", length = 255)
	private String launchedAt;

	@Column(name = "terminated_at", length = 255)
	private String terminatedAt;

	@Column(name = "host", length = 255)
	private String host;

	@Column(name = "instance_name", length = 255)
	private String instanceName;

	@Column(name = "hypervisor_hostname", length = 255)
	private String hypervisorHostname;

	@Column(name = "task_state", length = 255)
	private String taskState;

	@Column(name = "vm_state", length = 255)
	private String vmState;

	@Column(name = "power_state", length = 255)
	private String powerState;

	@Column(name = "addresses", columnDefinition = "TEXT")
	private String addresses;

	@Column(name = "links", columnDefinition = "TEXT")
	private String links;

	@Column(name = "volumes_attached", columnDefinition = "TEXT")
	private String volumesAttached;

	@Column(name = "OpenStack_ip", length = 255)
	private String openStackIp;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "image_id", referencedColumnName = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Image image;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "flavor_id", referencedColumnName = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Flavor flavor;

//	@ManyToOne(fetch = FetchType.LAZY, optional = false)
//	@JoinColumn(name = "Security_group_id", referencedColumnName = "id", nullable = false)
//	@OnDelete(action = OnDeleteAction.CASCADE)
//	private OpenstackSecurityGroup securityGroup;

	@Column(name = "Security_group_id", length = 255)
	private String Security_group_id;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}


	public String getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getAccessIPv4() {
		return accessIPv4;
	}

	public void setAccessIPv4(String accessIPv4) {
		this.accessIPv4 = accessIPv4;
	}

	public String getAccessIPv6() {
		return accessIPv6;
	}

	public void setAccessIPv6(String accessIPv6) {
		this.accessIPv6 = accessIPv6;
	}

	public String getDiskConfig() {
		return diskConfig;
	}

	public void setDiskConfig(String diskConfig) {
		this.diskConfig = diskConfig;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public String getConfigDrive() {
		return configDrive;
	}

	public void setConfigDrive(String configDrive) {
		this.configDrive = configDrive;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getLaunchedAt() {
		return launchedAt;
	}

	public void setLaunchedAt(String launchedAt) {
		this.launchedAt = launchedAt;
	}

	public String getTerminatedAt() {
		return terminatedAt;
	}

	public void setTerminatedAt(String terminatedAt) {
		this.terminatedAt = terminatedAt;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getHypervisorHostname() {
		return hypervisorHostname;
	}

	public void setHypervisorHostname(String hypervisorHostname) {
		this.hypervisorHostname = hypervisorHostname;
	}

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public String getVmState() {
		return vmState;
	}

	public void setVmState(String vmState) {
		this.vmState = vmState;
	}

	public String getPowerState() {
		return powerState;
	}

	public void setPowerState(String powerState) {
		this.powerState = powerState;
	}

	public String getAddresses() {
		return addresses;
	}

	public void setAddresses(String addresses) {
		this.addresses = addresses;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public String getVolumesAttached() {
		return volumesAttached;
	}

	public void setVolumesAttached(String volumesAttached) {
		this.volumesAttached = volumesAttached;
	}

	public String getOpenStackIp() {
		return openStackIp;
	}

	public void setOpenStackIp(String openStackIp) {
		this.openStackIp = openStackIp;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Flavor getFlavor() {
		return flavor;
	}

	public void setFlavor(Flavor flavor) {
		this.flavor = flavor;
	}

	public String getSecurity_group_id() {
		return Security_group_id;
	}

	public void setSecurity_group_id(String security_group_id) {
		Security_group_id = security_group_id;
	}

	

}

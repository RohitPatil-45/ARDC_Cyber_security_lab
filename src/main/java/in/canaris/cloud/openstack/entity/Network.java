package in.canaris.cloud.openstack.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "openstack_networks")
public class Network implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sr_no", updatable = false, nullable = false)
	private int srNo;

	@Column(name = "id", nullable = false, length = 255)
	private String id;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "tenant_id", nullable = false, length = 255)
	private String tenantId;

	@Column(name = "admin_state_up", nullable = false)
	private int adminStateUp;

	@Column(name = "mtu", nullable = false)
	private int mtu;

	@Column(name = "status", nullable = false, length = 255)
	private String status;

	@Column(name = "subnets", columnDefinition = "TEXT")
	private String subnets;

	@Column(name = "shared", nullable = false)
	private int shared;

	@Column(name = "availability_zone_hints", columnDefinition = "TEXT")
	private String availabilityZoneHints;

	@Column(name = "availability_zones", columnDefinition = "TEXT")
	private String availabilityZones;

	@Column(name = "ipv4_address_scope", length = 255)
	private String ipv4AddressScope;

	@Column(name = "ipv6_address_scope", length = 255)
	private String ipv6AddressScope;

	@Column(name = "router_external", nullable = false)
	private int routerExternal;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "port_security_enabled", nullable = false)
	private int portSecurityEnabled;

	@Column(name = "tags", columnDefinition = "TEXT")
	private String tags;

	@Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT '0000-00-00 00:00:00'")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Column(name = "revision_number")
	private Integer revisionNumber;

	@Column(name = "project_id", nullable = false, length = 255)
	private String projectId;

	@Column(name = "provider_network_type", length = 255)
	private String providerNetworkType;

	@Column(name = "provider_physical_network", length = 255)
	private String providerPhysicalNetwork;

	@Column(name = "provider_segmentation_id", length = 255)
	private String providerSegmentationId;

	@Column(name = "Openstack_ip", length = 255)
	private String openstackIp;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public int getAdminStateUp() {
		return adminStateUp;
	}

	public void setAdminStateUp(int adminStateUp) {
		this.adminStateUp = adminStateUp;
	}

	public int getMtu() {
		return mtu;
	}

	public void setMtu(int mtu) {
		this.mtu = mtu;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubnets() {
		return subnets;
	}

	public void setSubnets(String subnets) {
		this.subnets = subnets;
	}

	public int getShared() {
		return shared;
	}

	public void setShared(int shared) {
		this.shared = shared;
	}

	public String getAvailabilityZoneHints() {
		return availabilityZoneHints;
	}

	public void setAvailabilityZoneHints(String availabilityZoneHints) {
		this.availabilityZoneHints = availabilityZoneHints;
	}

	public String getAvailabilityZones() {
		return availabilityZones;
	}

	public void setAvailabilityZones(String availabilityZones) {
		this.availabilityZones = availabilityZones;
	}

	public String getIpv4AddressScope() {
		return ipv4AddressScope;
	}

	public void setIpv4AddressScope(String ipv4AddressScope) {
		this.ipv4AddressScope = ipv4AddressScope;
	}

	public String getIpv6AddressScope() {
		return ipv6AddressScope;
	}

	public void setIpv6AddressScope(String ipv6AddressScope) {
		this.ipv6AddressScope = ipv6AddressScope;
	}

	public int getRouterExternal() {
		return routerExternal;
	}

	public void setRouterExternal(int routerExternal) {
		this.routerExternal = routerExternal;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPortSecurityEnabled() {
		return portSecurityEnabled;
	}

	public void setPortSecurityEnabled(int portSecurityEnabled) {
		this.portSecurityEnabled = portSecurityEnabled;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getRevisionNumber() {
		return revisionNumber;
	}

	public void setRevisionNumber(Integer revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProviderNetworkType() {
		return providerNetworkType;
	}

	public void setProviderNetworkType(String providerNetworkType) {
		this.providerNetworkType = providerNetworkType;
	}

	public String getProviderPhysicalNetwork() {
		return providerPhysicalNetwork;
	}

	public void setProviderPhysicalNetwork(String providerPhysicalNetwork) {
		this.providerPhysicalNetwork = providerPhysicalNetwork;
	}

	public String getProviderSegmentationId() {
		return providerSegmentationId;
	}

	public void setProviderSegmentationId(String providerSegmentationId) {
		this.providerSegmentationId = providerSegmentationId;
	}

	public String getOpenstackIp() {
		return openstackIp;
	}

	public void setOpenstackIp(String openstackIp) {
		this.openstackIp = openstackIp;
	}

}

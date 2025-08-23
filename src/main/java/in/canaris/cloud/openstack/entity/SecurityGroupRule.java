package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "openstack_security_group_rules", indexes = {
		@Index(name = "security_group_id", columnList = "security_group_id") })
public class SecurityGroupRule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Sr_no", nullable = false)
	private int srNo;

	@Column(name = "security_group_rules_id", nullable = false, length = 255)
	private String securityGroupRulesId;

	@Column(name = "tenant_id", nullable = false, length = 255)
	private String tenantId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "security_group_id", referencedColumnName = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private OpenstackSecurityGroup securityGroup;

	@Column(name = "ethertype", nullable = false, length = 255)
	private String ethertype;

	@Column(name = "direction", nullable = false, length = 255)
	private String direction;

	@Column(name = "protocol", nullable = false, length = 255)
	private String protocol;

	@Column(name = "port_range_min", length = 255)
	private String portRangeMin;

	@Column(name = "port_range_max", length = 255)
	private String portRangeMax;

	@Column(name = "remote_ip_prefix", length = 255)
	private String remoteIpPrefix;

	@Column(name = "remote_address_group_id", length = 255)
	private String remoteAddressGroupId;

	@Column(name = "normalized_cidr", length = 255)
	private String normalizedCidr;

	@Column(name = "remote_group_id", length = 255)
	private String remoteGroupId;

	@Column(name = "standard_attr_id", length = 255)
	private String standardAttrId;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "tags", columnDefinition = "TEXT")
	private String tags;

	@Column(name = "created_at")
	private String createdAt;

	@Column(name = "updated_at")
	private String updatedAt;

	@Column(name = "revision_number", length = 255)
	private String revisionNumber;

	@Column(name = "project_id", nullable = false, length = 255)
	private String projectId;

	@Column(name = "Openstack_ip", length = 255)
	private String openstackIp;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getSecurityGroupRulesId() {
		return securityGroupRulesId;
	}

	public void setSecurityGroupRulesId(String securityGroupRulesId) {
		this.securityGroupRulesId = securityGroupRulesId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public OpenstackSecurityGroup getSecurityGroup() {
		return securityGroup;
	}

	public void setSecurityGroup(OpenstackSecurityGroup securityGroup) {
		this.securityGroup = securityGroup;
	}

	public String getEthertype() {
		return ethertype;
	}

	public void setEthertype(String ethertype) {
		this.ethertype = ethertype;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getPortRangeMin() {
		return portRangeMin;
	}

	public void setPortRangeMin(String portRangeMin) {
		this.portRangeMin = portRangeMin;
	}

	public String getPortRangeMax() {
		return portRangeMax;
	}

	public void setPortRangeMax(String portRangeMax) {
		this.portRangeMax = portRangeMax;
	}

	public String getRemoteIpPrefix() {
		return remoteIpPrefix;
	}

	public void setRemoteIpPrefix(String remoteIpPrefix) {
		this.remoteIpPrefix = remoteIpPrefix;
	}

	public String getRemoteAddressGroupId() {
		return remoteAddressGroupId;
	}

	public void setRemoteAddressGroupId(String remoteAddressGroupId) {
		this.remoteAddressGroupId = remoteAddressGroupId;
	}

	public String getNormalizedCidr() {
		return normalizedCidr;
	}

	public void setNormalizedCidr(String normalizedCidr) {
		this.normalizedCidr = normalizedCidr;
	}

	public String getRemoteGroupId() {
		return remoteGroupId;
	}

	public void setRemoteGroupId(String remoteGroupId) {
		this.remoteGroupId = remoteGroupId;
	}

	public String getStandardAttrId() {
		return standardAttrId;
	}

	public void setStandardAttrId(String standardAttrId) {
		this.standardAttrId = standardAttrId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getRevisionNumber() {
		return revisionNumber;
	}

	public void setRevisionNumber(String revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getOpenstackIp() {
		return openstackIp;
	}

	public void setOpenstackIp(String openstackIp) {
		this.openstackIp = openstackIp;
	}

}

package in.canaris.cloud.openstack.entity;

import javax.persistence.*;

@Entity
@Table(name = "openstack_projects")
public class projects {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sr_no", updatable = false, nullable = false)
	private int srNo;

	@Column(name = "OpenStack_ip")
	private String openStackIp;

	@Column(name = "projectId")
	private String projectId;

	@Column(name = "projectName")
	private String projectName;

	@Column(name = "projectDomainId")
	private String projectDomainId;

	@Column(name = "projectDescription", columnDefinition = "TEXT")
	private String projectDescription;

	@Column(name = "projectEnabled")
	private String projectEnabled;

	@Column(name = "projectParentId")
	private String projectParentId;

	@Column(name = "projectIsDomain")
	private String projectIsDomain;

	@Column(name = "links", columnDefinition = "TEXT")
	private String links;

	@Column(name = "tags", columnDefinition = "TEXT")
	private String tags;

	@Column(name = "options", columnDefinition = "TEXT")
	private String options;

	@Column(name = "cores")
	private String cores;

	@Column(name = "fixedIps")
	private String fixedIps;

	@Column(name = "floatingIps")
	private String floatingIps;

	@Column(name = "injectedFileContentBytes")
	private String injectedFileContentBytes;

	@Column(name = "injectedFilePathBytes")
	private String injectedFilePathBytes;

	@Column(name = "injectedFiles")
	private String injectedFiles;

	@Column(name = "instances")
	private String instances;

	@Column(name = "keyPairs")
	private String keyPairs;

	@Column(name = "metadataItems")
	private String metadataItems;

	@Column(name = "ram")
	private String ram;

	@Column(name = "securityGroupRules")
	private String securityGroupRules;

	@Column(name = "securityGroups")
	private String securityGroups;

	@Column(name = "serverGroupMembers")
	private String serverGroupMembers;

	@Column(name = "serverGroups")
	private String serverGroups;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getOpenStackIp() {
		return openStackIp;
	}

	public void setOpenStackIp(String openStackIp) {
		this.openStackIp = openStackIp;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDomainId() {
		return projectDomainId;
	}

	public void setProjectDomainId(String projectDomainId) {
		this.projectDomainId = projectDomainId;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getProjectEnabled() {
		return projectEnabled;
	}

	public void setProjectEnabled(String projectEnabled) {
		this.projectEnabled = projectEnabled;
	}

	public String getProjectParentId() {
		return projectParentId;
	}

	public void setProjectParentId(String projectParentId) {
		this.projectParentId = projectParentId;
	}

	public String getProjectIsDomain() {
		return projectIsDomain;
	}

	public void setProjectIsDomain(String projectIsDomain) {
		this.projectIsDomain = projectIsDomain;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getCores() {
		return cores;
	}

	public void setCores(String cores) {
		this.cores = cores;
	}

	public String getFixedIps() {
		return fixedIps;
	}

	public void setFixedIps(String fixedIps) {
		this.fixedIps = fixedIps;
	}

	public String getFloatingIps() {
		return floatingIps;
	}

	public void setFloatingIps(String floatingIps) {
		this.floatingIps = floatingIps;
	}

	public String getInjectedFileContentBytes() {
		return injectedFileContentBytes;
	}

	public void setInjectedFileContentBytes(String injectedFileContentBytes) {
		this.injectedFileContentBytes = injectedFileContentBytes;
	}

	public String getInjectedFilePathBytes() {
		return injectedFilePathBytes;
	}

	public void setInjectedFilePathBytes(String injectedFilePathBytes) {
		this.injectedFilePathBytes = injectedFilePathBytes;
	}

	public String getInjectedFiles() {
		return injectedFiles;
	}

	public void setInjectedFiles(String injectedFiles) {
		this.injectedFiles = injectedFiles;
	}

	public String getInstances() {
		return instances;
	}

	public void setInstances(String instances) {
		this.instances = instances;
	}

	public String getKeyPairs() {
		return keyPairs;
	}

	public void setKeyPairs(String keyPairs) {
		this.keyPairs = keyPairs;
	}

	public String getMetadataItems() {
		return metadataItems;
	}

	public void setMetadataItems(String metadataItems) {
		this.metadataItems = metadataItems;
	}

	public String getRam() {
		return ram;
	}

	public void setRam(String ram) {
		this.ram = ram;
	}

	public String getSecurityGroupRules() {
		return securityGroupRules;
	}

	public void setSecurityGroupRules(String securityGroupRules) {
		this.securityGroupRules = securityGroupRules;
	}

	public String getSecurityGroups() {
		return securityGroups;
	}

	public void setSecurityGroups(String securityGroups) {
		this.securityGroups = securityGroups;
	}

	public String getServerGroupMembers() {
		return serverGroupMembers;
	}

	public void setServerGroupMembers(String serverGroupMembers) {
		this.serverGroupMembers = serverGroupMembers;
	}

	public String getServerGroups() {
		return serverGroups;
	}

	public void setServerGroups(String serverGroups) {
		this.serverGroups = serverGroups;
	}

	// Getters and setters (omitted for brevity)

}

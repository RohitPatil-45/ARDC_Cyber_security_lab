package in.canaris.cloud.openstack.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "openstack_security_groups", indexes = { @Index(name = "id", columnList = "id"),
		@Index(name = "name", columnList = "name") })
public class OpenstackSecurityGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Sr_no", updatable = false, nullable = false)
	private int srNo;

	@Column(name = "id", length = 255)
	private String id;

	@Column(name = "name", length = 255)
	private String name;

	@Column(name = "stateful", nullable = false)
	private int stateful;

	@Column(name = "tenant_id", nullable = false, length = 255)
	private String tenantId;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "tags", columnDefinition = "TEXT")
	private String tags;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Column(name = "revision_number")
	private Integer revisionNumber;

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

	public int getStateful() {
		return stateful;
	}

	public void setStateful(int stateful) {
		this.stateful = stateful;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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

	public String getOpenstackIp() {
		return openstackIp;
	}

	public void setOpenstackIp(String openstackIp) {
		this.openstackIp = openstackIp;
	}

	// Getters and setters omitted for brevity

}

package in.canaris.cloud.openstack.entity;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "openstack_vm_request")
public class OpenstackVMRequest implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;
	
	@Column(name="instance_name")
	private String instanceName;
	
	@Column(name="image")
	private String image;
	
	@Column(name="flavor")
	private String flavor;

	@Column(name="request_type")
	private String requestType;

	@Column(name="request_by")
	private String requestBy;
	
	@Column(name="request_on")
	@CreationTimestamp
	private Timestamp requestOn;
	
	@Column(name="admin_approval_status")
	private String adminApprovalStatus;
	
	@Column(name="final_approval_status")
	private String finalApprovalStatus;
	
	@Column(name="approver_name")
	private String approverName;
	
	@Column(name="approved_on")
	private Timestamp approvedOn;
	
	@Column(name="description")
	private String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getFlavor() {
		return flavor;
	}

	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestBy() {
		return requestBy;
	}

	public void setRequestBy(String requestBy) {
		this.requestBy = requestBy;
	}

	public Timestamp getRequestOn() {
		return requestOn;
	}

	public void setRequestOn(Timestamp requestOn) {
		this.requestOn = requestOn;
	}

	public String getAdminApprovalStatus() {
		return adminApprovalStatus;
	}

	public void setAdminApprovalStatus(String adminApprovalStatus) {
		this.adminApprovalStatus = adminApprovalStatus;
	}

	public String getApproverName() {
		return approverName;
	}

	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	public Timestamp getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Timestamp approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getFinalApprovalStatus() {
		return finalApprovalStatus;
	}

	public void setFinalApprovalStatus(String finalApprovalStatus) {
		this.finalApprovalStatus = finalApprovalStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}

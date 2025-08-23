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
@Table(name = "request_approval")
public class RequestApproval implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private long id;
	
	@Column(name="request_id")
	private long requestId;
	
	@Column
	private String request_type;
	
	@Column
	private String sub_request_type;
	
	@Column
	private String request_status;

	@Column
	private String requesterName;
	
	@Column
	private String adminApproval;
	
	@Column
	@CreationTimestamp
	private Timestamp request_on;
	
	@Column
	private String approver_name;
	
	@Column
	private Timestamp approved_on;
	
	@Column
	private String description;
	
	@Column(name = "update_column_list")
	private String updateColumnList;
	
	@Column
	private String oldData;
	
	@Column
	private String newData;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "log_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private CloudInstanceLog log_id; 


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public String getRequest_type() {
		return request_type;
	}

	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}

	public String getRequest_status() {
		return request_status;
	}
	

	public String getSub_request_type() {
		return sub_request_type;
	}

	public void setSub_request_type(String sub_request_type) {
		this.sub_request_type = sub_request_type;
	}

	public void setRequest_status(String request_status) {
		this.request_status = request_status;
	}


	public String getRequesterName() {
		return requesterName;
	}

	
	public String getAdminApproval() {
		return adminApproval;
	}

	public void setAdminApproval(String adminApproval) {
		this.adminApproval = adminApproval;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	public Timestamp getRequest_on() {
		return request_on;
	}

	public void setRequest_on(Timestamp request_on) {
		this.request_on = request_on;
	}

	public String getApprover_name() {
		return approver_name;
	}

	public void setApprover_name(String approver_name) {
		this.approver_name = approver_name;
	}

	public Timestamp getApproved_on() {
		return approved_on;
	}

	public void setApproved_on(Timestamp approved_on) {
		this.approved_on = approved_on;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpdateColumnList() {
		return updateColumnList;
	}

	public void setUpdateColumnList(String updateColumnList) {
		this.updateColumnList = updateColumnList;
	}

	public CloudInstanceLog getLog_id() {
		return log_id;
	}

	public void setLog_id(CloudInstanceLog log_id) {
		this.log_id = log_id;
	}

	public String getOldData() {
		return oldData;
	}

	public void setOldData(String oldData) {
		this.oldData = oldData;
	}

	public String getNewData() {
		return newData;
	}

	public void setNewData(String newData) {
		this.newData = newData;
	}
	
	
}
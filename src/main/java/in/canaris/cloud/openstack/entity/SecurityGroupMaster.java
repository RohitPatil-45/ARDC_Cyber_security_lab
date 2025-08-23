package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "openstack_security_groups_master", indexes = {
		@Index(name = "security_group_name", columnList = "security_group_name"),
		@Index(name = "instance_security_group_id", columnList = "instance_security_group_id") })
public class SecurityGroupMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sr_no", nullable = false)
	private int srNo;

//	@OneToMany(fetch = FetchType.LAZY, optional = false)
//	@JoinColumn(name = "security_group_name", nullable = false)
//	@OnDelete(action = OnDeleteAction.CASCADE)
//	private OpenstackSecurityGroup securityGroup;

	@Column(name = "security_group_name", nullable = false, length = 255)
	private String security_group_name;

	@Column(name = "instance_security_group_id", nullable = false, length = 255)
	private String instanceSecurityGroupId;

	@Column(name = "Openstack_ip", length = 255)
	private String openstackIp;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getSecurity_group_name() {
		return security_group_name;
	}

	public void setSecurity_group_name(String security_group_name) {
		this.security_group_name = security_group_name;
	}

	public String getInstanceSecurityGroupId() {
		return instanceSecurityGroupId;
	}

	public void setInstanceSecurityGroupId(String instanceSecurityGroupId) {
		this.instanceSecurityGroupId = instanceSecurityGroupId;
	}

	public String getOpenstackIp() {
		return openstackIp;
	}

	public void setOpenstackIp(String openstackIp) {
		this.openstackIp = openstackIp;
	}

}

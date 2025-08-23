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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "security_group_rules")
public class SecurityGroupRules implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column
	private String type;

	@Column
	private String protocol;
	
	@Column
	private String port_range;
	
	@Column
	private String source_name;
	
	@Column
	private String source_ip;
	
	@Column
	private String description;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "security_group_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private SecurityGroup security_group_id;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getProtocol() {
		return protocol;
	}


	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}


	public String getPort_range() {
		return port_range;
	}


	public void setPort_range(String port_range) {
		this.port_range = port_range;
	}


	public String getSource_name() {
		return source_name;
	}


	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}


	public String getSource_ip() {
		return source_ip;
	}


	public void setSource_ip(String source_ip) {
		this.source_ip = source_ip;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public SecurityGroup getSecurity_group_id() {
		return security_group_id;
	}


	public void setSecurity_group_id(SecurityGroup security_group_id) {
		this.security_group_id = security_group_id;
	}
	
}

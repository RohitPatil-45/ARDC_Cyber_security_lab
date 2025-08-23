package in.canaris.cloud.server.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "actived_config_master")
public class AactiveDConfig implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;
	
	@Column
	private String ldap_server;
	
	@Column
	private String logon_domain;
	
	@Column
	private String server_port;
	
	@Column
	private String username;
	
	@Column
	private String password;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLdap_server() {
		return ldap_server;
	}

	public void setLdap_server(String ldap_server) {
		this.ldap_server = ldap_server;
	}

	public String getLogon_domain() {
		return logon_domain;
	}

	public void setLogon_domain(String logon_domain) {
		this.logon_domain = logon_domain;
	}

	public String getServer_port() {
		return server_port;
	}

	public void setServer_port(String server_port) {
		this.server_port = server_port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

	
}

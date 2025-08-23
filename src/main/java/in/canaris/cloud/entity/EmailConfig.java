package in.canaris.cloud.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "emailConfig_master")
public class EmailConfig implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;
	
	@Column
	private String email_id;
	
	@Column
	private String is_ssl_tls;
	
	@Column
	private String username;
	
	@Column
	private String password;
	
	@Column
	private String port;
	
	@Column
	private String smtp_auth;
	
	@Column
	private String smtp_server;
	
	@Column
	private String timeout;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail_id() {
		return email_id;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}

	public String getIs_ssl_tls() {
		return is_ssl_tls;
	}

	public void setIs_ssl_tls(String is_ssl_tls) {
		this.is_ssl_tls = is_ssl_tls;
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

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSmtp_auth() {
		return smtp_auth;
	}

	public void setSmtp_auth(String smtp_auth) {
		this.smtp_auth = smtp_auth;
	}

	public String getSmtp_server() {
		return smtp_server;
	}

	public void setSmtp_server(String smtp_server) {
		this.smtp_server = smtp_server;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}


}

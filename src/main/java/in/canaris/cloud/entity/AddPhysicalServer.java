package in.canaris.cloud.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "add_physical_server")
public class AddPhysicalServer implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private long id;

	@Column(name = "server_ip")
	private String serverIP;

	@Column(name = "hostname")
	private String hostname;

	@Column(name = "last_sync_time")
	private Timestamp lastSyncTime;

	@Column(name = "virtualization_type")
	private String virtualization_type;

	@Column(name = "ssh_host")
	private String ssh_host;

	@Column(name = "ssh_password")
	private String ssh_password;
	
	@Column(name = "status")
	private String status;

	 
	public String getSsh_host() {
		return ssh_host;
	}

	public void setSsh_host(String ssh_host) {
		this.ssh_host = ssh_host;
	}

	public String getSsh_password() {
		return ssh_password;
	}

	public void setSsh_password(String ssh_password) {
		this.ssh_password = ssh_password;
	}

	public String getVirtualization_type() {
		return virtualization_type;
	}

	public void setVirtualization_type(String virtualization_type) {
		this.virtualization_type = virtualization_type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Timestamp getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(Timestamp lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}

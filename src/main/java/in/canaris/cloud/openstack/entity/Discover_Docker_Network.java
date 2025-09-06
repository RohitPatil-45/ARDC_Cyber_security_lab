package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "discover_docker_network")
public class Discover_Docker_Network {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "name", nullable = false, length = 255)
	private String Name;
	
	@Column(name = "network_id", nullable = false, length = 255)
	private String NetworkId;
	
	@Column(name = "driver", nullable = false, length = 255)
	private String Driver;
	
	@Column(name = "scope", nullable = false, length = 255)
	private String Scope;
	
	@Column(name = "gateway", nullable = false, length = 255)
	private String Gateway;
	
	@Column(name = "start_ip", nullable = false, length = 255)
	private String StartIp;
	
	@Column(name = "end_ip", nullable = false, length = 255)
	private String EndIp;
	
	@Column(name = "physicalserver", nullable = false, length = 255)
	private String PhysicalServer;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getNetworkId() {
		return NetworkId;
	}

	public void setNetworkId(String networkId) {
		NetworkId = networkId;
	}

	public String getDriver() {
		return Driver;
	}

	public void setDriver(String driver) {
		Driver = driver;
	}

	public String getScope() {
		return Scope;
	}

	public void setScope(String scope) {
		Scope = scope;
	}

	public String getGateway() {
		return Gateway;
	}

	public void setGateway(String gateway) {
		Gateway = gateway;
	}

	public String getStartIp() {
		return StartIp;
	}

	public void setStartIp(String startIp) {
		StartIp = startIp;
	}

	public String getEndIp() {
		return EndIp;
	}

	public void setEndIp(String endIp) {
		EndIp = endIp;
	}

	public String getPhysicalServer() {
		return PhysicalServer;
	}

	public void setPhysicalServer(String physicalServer) {
		PhysicalServer = physicalServer;
	}

	@Override
	public String toString() {
		return "Discover_Docker_Network [Id=" + Id + ", Name=" + Name + ", NetworkId=" + NetworkId + ", Driver="
				+ Driver + ", Scope=" + Scope + ", Gateway=" + Gateway + ", StartIp=" + StartIp + ", EndIp=" + EndIp
				+ ", PhysicalServer=" + PhysicalServer + "]";
	}
	
	
	
	
	
	

}

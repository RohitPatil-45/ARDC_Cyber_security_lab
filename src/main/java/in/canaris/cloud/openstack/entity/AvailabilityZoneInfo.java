package in.canaris.cloud.openstack.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "openstack_availability_zone_info")
public class AvailabilityZoneInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sr_no", updatable = false, nullable = false)
	private int srNo;

	@Column(name = "zone_name", nullable = false, length = 255)
	private String zoneName;

	@Column(name = "available", nullable = false, length = 255)
	private String available;

	@Column(name = "hosts", length = 255)
	private String hosts;

	@Column(name = "OpenStack_ip", length = 255)
	private String openStackIp;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public String getOpenStackIp() {
		return openStackIp;
	}

	public void setOpenStackIp(String openStackIp) {
		this.openStackIp = openStackIp;
	}

}

package in.canaris.cloud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "port_details")
public class PortDetails {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "vnc_port", nullable = false)
    private Integer vncPort;

    @Column(name = "no_vnc_port", nullable = false)
    private Integer noVncPort;

    @Column(name = "vm_name", nullable = false, length = 50)
    private String vmName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVncPort() {
		return vncPort;
	}

	public void setVncPort(Integer vncPort) {
		this.vncPort = vncPort;
	}

	public Integer getNoVncPort() {
		return noVncPort;
	}

	public void setNoVncPort(Integer noVncPort) {
		this.noVncPort = noVncPort;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
    
}

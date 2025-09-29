package in.canaris.cloud.openstack.entity;

import javax.persistence.*;

@Entity
@Table(name = "physical_server_health_monitoring")
public class PhysicalServerHealthMonitoring {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;
  
    @Column(name = "physical_server_ip", nullable = false, unique = true)
    private int physicalServerIp;

    @Column(name = "free_ram")
    private double freeRam;

    @Column(name = "used_ram")
    private double usedRam;

    @Column(name = "total_ram")
    private double totalRam;

    @Column(name = "free_cpu")
    private double freeCpu;

    @Column(name = "used_cpu")
    private double usedCpu;

    @Column(name = "total_cpu")
    private double totalCpu;

    @Column(name = "free_disk")
    private double freeDisk;

    @Column(name = "used_disk")
    private double usedDisk;

    @Column(name = "total_disk")
    private double totalDisk;

   

    public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getPhysicalServerIp() {
		return physicalServerIp;
	}

	public void setPhysicalServerIp(int physicalServerIp) {
		this.physicalServerIp = physicalServerIp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public double getFreeRam() {
        return freeRam;
    }

    public void setFreeRam(double freeRam) {
        this.freeRam = freeRam;
    }

    public double getUsedRam() {
        return usedRam;
    }

    public void setUsedRam(double usedRam) {
        this.usedRam = usedRam;
    }

    public double getTotalRam() {
        return totalRam;
    }

    public void setTotalRam(double totalRam) {
        this.totalRam = totalRam;
    }

    public double getFreeCpu() {
        return freeCpu;
    }

    public void setFreeCpu(double freeCpu) {
        this.freeCpu = freeCpu;
    }

    public double getUsedCpu() {
        return usedCpu;
    }

    public void setUsedCpu(double usedCpu) {
        this.usedCpu = usedCpu;
    }

    public double getTotalCpu() {
        return totalCpu;
    }

    public void setTotalCpu(double totalCpu) {
        this.totalCpu = totalCpu;
    }

    public double getFreeDisk() {
        return freeDisk;
    }

    public void setFreeDisk(double freeDisk) {
        this.freeDisk = freeDisk;
    }

    public double getUsedDisk() {
        return usedDisk;
    }

    public void setUsedDisk(double usedDisk) {
        this.usedDisk = usedDisk;
    }

    public double getTotalDisk() {
        return totalDisk;
    }

    public void setTotalDisk(double totalDisk) {
        this.totalDisk = totalDisk;
    }
}

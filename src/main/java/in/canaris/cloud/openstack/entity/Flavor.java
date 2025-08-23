package in.canaris.cloud.openstack.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "openstack_flavors", indexes = { @Index(name = "id", columnList = "id") })
public class Flavor implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Sr_no", updatable = false, nullable = false)
	private int srNo;

	@Column(name = "id", nullable = false, length = 255)
	private String id;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "ram", nullable = false)
	private int ram;

	@Column(name = "disk", nullable = false)
	private int disk;

	@Column(name = "swap", length = 255)
	private String swap;

	@Column(name = "ephemeral", nullable = false)
	private int ephemeral;

	@Column(name = "disabled", nullable = false, columnDefinition = "int default 0")
	private int disabled;

	@Column(name = "vcpus", nullable = false)
	private int vcpus;

	@Column(name = "is_public", nullable = false, columnDefinition = "int default 0")
	private int isPublic;

	@Column(name = "rxtx_factor", nullable = false)
	private double rxtxFactor;

	@Column(name = "links", columnDefinition = "TEXT")
	private String links;

	@Column(name = "OpenStack_ip", length = 255)
	private String openStackIp;

	@Column(name = "ephemeralDisk", length = 255)
	private String ephemeralDisk;

	@Column(name = "flavorID", length = 255)
	private String flavorID;

	@Column(name = "flavorName", length = 255)
	private String flavorName;

	@Column(name = "rootDisk", length = 255)
	private String rootDisk;

	@Column(name = "rxTxFactor", length = 255)
	private String rxTxFactorAlt;

	@Column(name = "swapDisk", length = 255)
	private String swapDisk;

	@Column(name = "vCPU", length = 255)
	private String vCPU;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public int getDisk() {
		return disk;
	}

	public void setDisk(int disk) {
		this.disk = disk;
	}

	public String getSwap() {
		return swap;
	}

	public void setSwap(String swap) {
		this.swap = swap;
	}

	public int getEphemeral() {
		return ephemeral;
	}

	public void setEphemeral(int ephemeral) {
		this.ephemeral = ephemeral;
	}

	public int getDisabled() {
		return disabled;
	}

	public void setDisabled(int disabled) {
		this.disabled = disabled;
	}

	public int getVcpus() {
		return vcpus;
	}

	public void setVcpus(int vcpus) {
		this.vcpus = vcpus;
	}

	public int getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}

	public double getRxtxFactor() {
		return rxtxFactor;
	}

	public void setRxtxFactor(double rxtxFactor) {
		this.rxtxFactor = rxtxFactor;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public String getOpenStackIp() {
		return openStackIp;
	}

	public void setOpenStackIp(String openStackIp) {
		this.openStackIp = openStackIp;
	}

	public String getEphemeralDisk() {
		return ephemeralDisk;
	}

	public void setEphemeralDisk(String ephemeralDisk) {
		this.ephemeralDisk = ephemeralDisk;
	}

	public String getFlavorID() {
		return flavorID;
	}

	public void setFlavorID(String flavorID) {
		this.flavorID = flavorID;
	}

	public String getFlavorName() {
		return flavorName;
	}

	public void setFlavorName(String flavorName) {
		this.flavorName = flavorName;
	}

	public String getRootDisk() {
		return rootDisk;
	}

	public void setRootDisk(String rootDisk) {
		this.rootDisk = rootDisk;
	}

	public String getRxTxFactorAlt() {
		return rxTxFactorAlt;
	}

	public void setRxTxFactorAlt(String rxTxFactorAlt) {
		this.rxTxFactorAlt = rxTxFactorAlt;
	}

	public String getSwapDisk() {
		return swapDisk;
	}

	public void setSwapDisk(String swapDisk) {
		this.swapDisk = swapDisk;
	}

	public String getvCPU() {
		return vCPU;
	}

	public void setvCPU(String vCPU) {
		this.vCPU = vCPU;
	}

}

package in.canaris.cloud.openstack.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "openstack_keypairs")
public class KeyPair implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sr_no", updatable = false, nullable = false)
	private int srNo;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
	private String publicKey;

	@Column(name = "fingerprint", nullable = false, length = 255)
	private String fingerprint;

	@Column(name = "Openstack_ip", length = 255)
	private String openstackIp;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public String getOpenstackIp() {
		return openstackIp;
	}

	public void setOpenstackIp(String openstackIp) {
		this.openstackIp = openstackIp;
	}

}

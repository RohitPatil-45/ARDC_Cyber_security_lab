package in.canaris.cloud.entity;
 
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
 
@Entity
@Table(name = "floating_ip_assign", //
        uniqueConstraints = { //
                @UniqueConstraint(name = "PUBLIC_IP_UK", columnNames = { "instance_ip", "public_ip" }) })


public class FloatingIPAssign implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;
 
    @Id
    @GeneratedValue
    @Column(name = "Id", nullable = false)
    private int id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_ip", nullable = false)
    private CloudInstance instance_ip;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "public_ip", nullable = false)
    private FloatingIP public_ip;
    
    @Column
	private String dns_domain;

    @Column
	private String dns_name;

    @Column
	private String description;

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CloudInstance getInstance_ip() {
		return instance_ip;
	}

	public void setInstance_ip(CloudInstance instance_ip) {
		this.instance_ip = instance_ip;
	}

	public FloatingIP getPublic_ip() {
		return public_ip;
	}

	public void setPublic_ip(FloatingIP public_ip) {
		this.public_ip = public_ip;
	}

	public String getDns_domain() {
		return dns_domain;
	}

	public void setDns_domain(String dns_domain) {
		this.dns_domain = dns_domain;
	}

	public String getDns_name() {
		return dns_name;
	}

	public void setDns_name(String dns_name) {
		this.dns_name = dns_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
 
    
    
 
     
}
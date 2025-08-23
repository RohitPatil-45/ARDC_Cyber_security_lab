package in.canaris.cloud.entity;


import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "vpc_master")
public class VPC implements Serializable{
	
	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;
	
//	@Column
//	private String dc_location;
	
	@Column
	private String vpc_name;

	@Column
	private String network;
	
	@Column
	private String network_size;

	@Column
	private String subnet;
	
	@Column
	private String status;
	
	
//	@ManyToOne
//	@JoinColumn(name="id")
//	private Set<Location> id;
	
	  @ManyToOne(fetch = FetchType.LAZY, optional = false)
	  @JoinColumn(name = "location_id", nullable = false)
	  @OnDelete(action = OnDeleteAction.NO_ACTION)
	  @JsonIgnore
	  private Location location_id;
	  
	  
	  


	public Location getLocation_id() {
		return location_id;
	}

	public void setLocation_id(Location location_id) {
		this.location_id = location_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

//	public String getDc_location() {
//		return dc_location;
//	}
//
//	public void setDc_location(String dc_location) {
//		this.dc_location = dc_location;
//	}

	public String getVpc_name() {
		return vpc_name;
	}

	public void setVpc_name(String vpc_name) {
		this.vpc_name = vpc_name;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getNetwork_size() {
		return network_size;
	}

	public void setNetwork_size(String network_size) {
		this.network_size = network_size;
	}

	public String getSubnet() {
		return subnet;
	}

	public void setSubnet(String subnet) {
		this.subnet = subnet;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

}

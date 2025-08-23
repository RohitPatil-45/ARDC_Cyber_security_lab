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
@Table(name = "price_master")
public class Price implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column(name = "HOURLY_PRICE")
	private double hourly_price;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "plan_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Plan plan_id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Product product_id;

	@Column(name = "RAM")
	private String ram;

	@Column(name = "vCPU")
	private String vCpu;

	@Column(name = "SSD_DISK")
	private String ssd_disk;

	@Column(name = "BANDWIDTH")
	private String bandwidth;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getHourly_price() {
		return hourly_price;
	}

	public void setHourly_price(double hourly_price) {
		this.hourly_price = hourly_price;
	}

	public Plan getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(Plan plan_id) {
		this.plan_id = plan_id;
	}

	public Product getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Product product_id) {
		this.product_id = product_id;
	}

	public String getRam() {
		return ram;
	}

	public void setRam(String ram) {
		this.ram = ram + "GB";
	}

	public String getvCpu() {
		return vCpu;
	}

	public void setvCpu(String vCpu) {
		this.vCpu = vCpu;
	}

	public String getSsd_disk() {
		return ssd_disk;
	}

	public void setSsd_disk(String ssd_disk) {
		this.ssd_disk = ssd_disk+"GB";
	}

	public String getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth+"MB";
	}

}

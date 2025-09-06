package in.canaris.cloud.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sub_product_master")
public class SubProduct implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;


	@Column
	private String sub_product_name;
	
	@Column
	private String iso_file_path;
	
	@Column
	private String variant;
	
	@Column(name = "is_source_created")
	private String isSourceCreated;
	
	@Column(name="os_name")
	private String osName;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Product product_id;
	

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getSub_product_name() {
		return sub_product_name;
	}


	public void setSub_product_name(String sub_product_name) {
		this.sub_product_name = sub_product_name;
	}


	public Product getProduct_id() {
		return product_id;
	}


	public void setProduct_id(Product product_id) {
		this.product_id = product_id;
	}


	public String getIso_file_path() {
		return iso_file_path;
	}


	public void setIso_file_path(String iso_file_path) {
		this.iso_file_path = iso_file_path;
	}


	public String getVariant() {
		return variant;
	}


	public void setVariant(String variant) {
		this.variant = variant;
	}


	public String getOsName() {
		return osName;
	}


	public void setOsName(String osName) {
		this.osName = osName;
	}


	public String getIsSourceCreated() {
		return isSourceCreated;
	}


	public void setIsSourceCreated(String isSourceCreated) {
		this.isSourceCreated = isSourceCreated;
	}
	
	
}

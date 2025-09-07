package in.canaris.cloud.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "additional_storage")
public class AdditionalStorage implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, nullable = false)
	private int id;

	@Column
	private String storage_size;

	@Column
	@CreationTimestamp
	private Timestamp create_time;
	
	@Column
	private int price;
	
	@Column
	private String status;
	
	@Column(name="storage_path")
	private String storagePath;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "instance_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private CloudInstance instance_id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStorage_size() {
		return storage_size;
	}

	public void setStorage_size(String storage_size) {
		this.storage_size = storage_size;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public CloudInstance getInstance_id() {
		return instance_id;
	}

	public void setInstance_id(CloudInstance instance_id) {
		this.instance_id = instance_id;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}

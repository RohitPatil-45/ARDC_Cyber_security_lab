package in.canaris.cloud.openstack.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "openstack_images" , indexes = { @Index(name = "id", columnList = "id") })
public class Image implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sr_no", updatable = false, nullable = false)
	private int srNo;

	@Column(name = "Openstack_IP", length = 255)
	private String openstackIp;

	@Column(name = "id", length = 255)
	private String imageId;

	@Column(name = "img_description", columnDefinition = "TEXT")
	private String imgDescription;

	@Column(name = "img_name", length = 255)
	private String imgName;

	@Column(name = "disk_format", length = 255)
	private String diskFormat;

	@Column(name = "container_format", length = 255)
	private String containerFormat;

	@Column(name = "visibility", length = 255)
	private String visibility;

	@Column(name = "size")
	private Long size;

	@Column(name = "virtual_size")
	private Long virtualSize;

	@Column(name = "img_status", length = 255)
	private String imgStatus;

	@Column(name = "img_checksum", length = 255)
	private String imgChecksum;

	@Column(name = "protected")
	private Integer isProtected;

	@Column(name = "min_ram")
	private Integer minRam;

	@Column(name = "min_disk")
	private Integer minDisk;

	@Column(name = "img_owner", length = 255)
	private String imgOwner;

	@Column(name = "os_hidden")
	private Integer osHidden;

	@Column(name = "os_hash_algo", length = 255)
	private String osHashAlgo;

	@Column(name = "os_hash_value", length = 255)
	private String osHashValue;

	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "updated_at")
	private Date updatedAt;

	@Column(name = "self", length = 255)
	private String self;

	@Column(name = "img_file", length = 255)
	private String imgFile;

	@Column(name = "img_schema", length = 255)
	private String imgSchema;

	@Column(name = "imageSource", length = 255)
	private String imageSource;

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getOpenstackIp() {
		return openstackIp;
	}

	public void setOpenstackIp(String openstackIp) {
		this.openstackIp = openstackIp;
	}

	
	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImgDescription() {
		return imgDescription;
	}

	public void setImgDescription(String imgDescription) {
		this.imgDescription = imgDescription;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public String getDiskFormat() {
		return diskFormat;
	}

	public void setDiskFormat(String diskFormat) {
		this.diskFormat = diskFormat;
	}

	public String getContainerFormat() {
		return containerFormat;
	}

	public void setContainerFormat(String containerFormat) {
		this.containerFormat = containerFormat;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getVirtualSize() {
		return virtualSize;
	}

	public void setVirtualSize(Long virtualSize) {
		this.virtualSize = virtualSize;
	}

	public String getImgStatus() {
		return imgStatus;
	}

	public void setImgStatus(String imgStatus) {
		this.imgStatus = imgStatus;
	}

	public String getImgChecksum() {
		return imgChecksum;
	}

	public void setImgChecksum(String imgChecksum) {
		this.imgChecksum = imgChecksum;
	}

	public Integer getIsProtected() {
		return isProtected;
	}

	public void setIsProtected(Integer isProtected) {
		this.isProtected = isProtected;
	}

	public Integer getMinRam() {
		return minRam;
	}

	public void setMinRam(Integer minRam) {
		this.minRam = minRam;
	}

	public Integer getMinDisk() {
		return minDisk;
	}

	public void setMinDisk(Integer minDisk) {
		this.minDisk = minDisk;
	}

	public String getImgOwner() {
		return imgOwner;
	}

	public void setImgOwner(String imgOwner) {
		this.imgOwner = imgOwner;
	}

	public Integer getOsHidden() {
		return osHidden;
	}

	public void setOsHidden(Integer osHidden) {
		this.osHidden = osHidden;
	}

	public String getOsHashAlgo() {
		return osHashAlgo;
	}

	public void setOsHashAlgo(String osHashAlgo) {
		this.osHashAlgo = osHashAlgo;
	}

	public String getOsHashValue() {
		return osHashValue;
	}

	public void setOsHashValue(String osHashValue) {
		this.osHashValue = osHashValue;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public String getImgFile() {
		return imgFile;
	}

	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
	}

	public String getImgSchema() {
		return imgSchema;
	}

	public void setImgSchema(String imgSchema) {
		this.imgSchema = imgSchema;
	}

	public String getImageSource() {
		return imageSource;
	}

	public void setImageSource(String imageSource) {
		this.imageSource = imageSource;
	}

}

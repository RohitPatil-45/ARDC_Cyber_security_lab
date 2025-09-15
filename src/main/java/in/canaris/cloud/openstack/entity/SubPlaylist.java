package in.canaris.cloud.openstack.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "subplaylist")
public class SubPlaylist {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "playlistName", nullable = false, length = 255)
	private String playlistName;

	@Column(name = "playlistTitle", nullable = false, length = 255)
	private String playlistTitle;

	@Lob
	@Column(name = "Description", nullable = true)
	private String Description;

	@Column(name = "Tag", nullable = false, length = 255)
	private String Tag;

	@Column(name = "CreatedBy", nullable = false, length = 255)
	private String CreatedBy;

	@Lob
	@Column(name = "coverImage", nullable = true)
	private byte[] CoverImage;

	@OneToMany(mappedBy = "subPlaylist", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SubPlaylistScenario> scenarios = new HashSet<>();

	public Set<SubPlaylistScenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(Set<SubPlaylistScenario> scenarios) {
		this.scenarios = scenarios;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getPlaylistName() {
		return playlistName;
	}

	public void setPlaylistName(String playlistName) {
		this.playlistName = playlistName;
	}

	public String getPlaylistTitle() {
		return playlistTitle;
	}

	public void setPlaylistTitle(String playlistTitle) {
		this.playlistTitle = playlistTitle;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getTag() {
		return Tag;
	}

	public void setTag(String tag) {
		Tag = tag;
	}

	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public byte[] getCoverImage() {
		return CoverImage;
	}

	public void setCoverImage(byte[] coverImage) {
		CoverImage = coverImage;
	}

}

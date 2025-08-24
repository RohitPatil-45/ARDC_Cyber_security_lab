package in.canaris.cloud.openstack.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "Playlist")
public class Playlist {

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

	@Lob
	@Column(name = "coverImage", nullable = true)
	private byte[] CoverImage;

	@Column(name = "Tag", nullable = false, length = 255)
	private String Tag;

	@Column(name = "CreatedBy", nullable = false, length = 255)
	private String CreatedBy;

	@ManyToMany
	@JoinTable(name = "playlist_scenario", // junction table
			joinColumns = @JoinColumn(name = "PlaylistId"), inverseJoinColumns = @JoinColumn(name = "ScenarioId"))
	private Set<Add_Scenario> scenarios = new HashSet<>();

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

	public byte[] getCoverImage() {
		return CoverImage;
	}

	public void setCoverImage(byte[] coverImage) {
		CoverImage = coverImage;
	}

	public String getTag() {
		return Tag;
	}

	public void setTag(String tag) {
		Tag = tag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCreatedBy() {
		return CreatedBy;
	}

	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}

	public Set<Add_Scenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(Set<Add_Scenario> scenarios) {
		this.scenarios = scenarios;
	}

}

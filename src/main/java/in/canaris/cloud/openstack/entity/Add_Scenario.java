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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "add_scenario")
public class Add_Scenario {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "ScenarioTitle", nullable = false, length = 255)
	private String ScenarioTitle;

	@Column(name = "ScenarioName", nullable = false, length = 255)
	private String ScenarioName;

	@Lob
	@Column(name = "Description", nullable = true)
	private String Description;

	@Column(name = "Category", nullable = false, length = 255)
	private String Category;

	@Column(name = "ScenarioType", nullable = false, length = 255)
	private String ScenarioType;

	@Column(name = "Mode", nullable = false, length = 255)
	private String Mode;

	@Column(name = "DifficultyLevel", nullable = false, length = 255)
	private String DifficultyLevel;

	@Column(name = "Duration", nullable = false, length = 255)
	private String Duration;

	@Column(name = "MaxPlayers", nullable = false, length = 255)
	private String MaxPlayers;

	@Column(name = "number_of_instance", nullable = false, length = 255)
	private String NumberofInstance;

//	@Column(name = "Cover_Image", nullable = false, length = 255)
//	private String Cover_Image;

//	@Lob // Large Object
//	@Column(columnDefinition = "LONGBLOB") // ✅ For MySQL. For Oracle, just "BLOB"
//	private byte[] coverImage;

	@Lob
	@Column(name = "coverImage", nullable = true) // Make sure nullable = true
	private byte[] coverImage;

	@Column(name = "Labs", nullable = false, length = 255)
	private String Labs;

//	@Column(name = "LabId", nullable = false, length = 255)
//	private String LabId;

	@Column(name = "Comments", nullable = false, length = 255)
	private String Comments;

	@ManyToMany(mappedBy = "scenarios")
	private Set<Playlist> playlists = new HashSet<>();

	@OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SubPlaylistScenario> subPlaylists = new HashSet<>();

	public Set<SubPlaylistScenario> getSubPlaylists() {
		return subPlaylists;
	}

	public void setSubPlaylists(Set<SubPlaylistScenario> subPlaylists) {
		this.subPlaylists = subPlaylists;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getScenarioTitle() {
		return ScenarioTitle;
	}

	public void setScenarioTitle(String scenarioTitle) {
		ScenarioTitle = scenarioTitle;
	}

	public String getScenarioName() {
		return ScenarioName;
	}

	public void setScenarioName(String scenarioName) {
		ScenarioName = scenarioName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}

	public String getScenarioType() {
		return ScenarioType;
	}

	public void setScenarioType(String scenarioType) {
		ScenarioType = scenarioType;
	}

	public String getMode() {
		return Mode;
	}

	public void setMode(String mode) {
		Mode = mode;
	}

	public String getDifficultyLevel() {
		return DifficultyLevel;
	}

	public void setDifficultyLevel(String difficultyLevel) {
		DifficultyLevel = difficultyLevel;
	}

	public String getDuration() {
		return Duration;
	}

	public void setDuration(String duration) {
		Duration = duration;
	}

	public String getMaxPlayers() {
		return MaxPlayers;
	}

	public void setMaxPlayers(String maxPlayers) {
		MaxPlayers = maxPlayers;
	}

	public byte[] getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(byte[] coverImage) {
		this.coverImage = coverImage;
	}

	public String getLabs() {
		return Labs;
	}

	public void setLabs(String labs) {
		Labs = labs;
	}

	public String getComments() {
		return Comments;
	}

	public void setComments(String comments) {
		Comments = comments;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

//	public String getLabId() {
//		return LabId;
//	}
//
//	public void setLabId(String labId) {
//		LabId = labId;
//	}

	public Set<Playlist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(Set<Playlist> playlists) {
		this.playlists = playlists;
	}

	public String getNumberofInstance() {
		return NumberofInstance;
	}

	public void setNumberofInstance(String numberofInstance) {
		NumberofInstance = numberofInstance;
	}

}

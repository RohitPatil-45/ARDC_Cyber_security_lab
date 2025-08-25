package in.canaris.cloud.entity;

import javax.persistence.*;

import in.canaris.cloud.openstack.entity.Add_Scenario;
import in.canaris.cloud.openstack.entity.Playlist;

@Entity
@Table(name = "playlist_scenario")
public class PlaylistScenario {

    @EmbeddedId
    private PlaylistScenarioId id;

    @ManyToOne
    @MapsId("playlistId") // maps to composite key field
    @JoinColumn(name = "PlaylistId", referencedColumnName = "Id")
    private Playlist playlist;

    @ManyToOne
    @MapsId("scenarioId") // maps to composite key field
    @JoinColumn(name = "ScenarioId", referencedColumnName = "Id")
    private Add_Scenario scenario;

    public PlaylistScenario() {}

    public PlaylistScenario(Playlist playlist, Add_Scenario scenario) {
        this.playlist = playlist;
        this.scenario = scenario;
        this.id = new PlaylistScenarioId(playlist.getId(), scenario.getId());
    }

    // getters and setters
    public PlaylistScenarioId getId() {
        return id;
    }

    public void setId(PlaylistScenarioId id) {
        this.id = id;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public Add_Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Add_Scenario scenario) {
        this.scenario = scenario;
    }
}

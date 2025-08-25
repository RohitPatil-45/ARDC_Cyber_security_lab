package in.canaris.cloud.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class PlaylistScenarioId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "PlaylistId")
    private int playlistId;

    @Column(name = "ScenarioId")
    private int scenarioId;

    public PlaylistScenarioId() {}

    public PlaylistScenarioId(int playlistId, int scenarioId) {
        this.playlistId = playlistId;
        this.scenarioId = scenarioId;
    }

    // getters and setters
    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(int scenarioId) {
        this.scenarioId = scenarioId;
    }

    // equals & hashCode (required for composite keys)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaylistScenarioId)) return false;
        PlaylistScenarioId that = (PlaylistScenarioId) o;
        return playlistId == that.playlistId &&
               scenarioId == that.scenarioId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId, scenarioId);
    }
}

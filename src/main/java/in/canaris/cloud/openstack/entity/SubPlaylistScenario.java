package in.canaris.cloud.openstack.entity;

import javax.persistence.*;

@Entity
@Table(name = "sub_playlist_scenarios")
public class SubPlaylistScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_playlist_scenario_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "sub_playlist_id", nullable = false)
    private SubPlaylist subPlaylist;

    @ManyToOne
    @JoinColumn(name = "scenario_id", nullable = false)
    private Add_Scenario scenario;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubPlaylist getSubPlaylist() {
        return subPlaylist;
    }

    public void setSubPlaylist(SubPlaylist subPlaylist) {
        this.subPlaylist = subPlaylist;
    }

    public Add_Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Add_Scenario scenario) {
        this.scenario = scenario;
    }
}

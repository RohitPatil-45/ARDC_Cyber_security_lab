package in.canaris.cloud.openstack.entity;

import java.util.List;

public class UserMappingsResponse {

	 private List<Integer> selectedPlaylists;
	    private List<Integer> selectedSubplaylists;
	    private List<Integer> selectedScenarios;

	    // Getters & Setters
	    public List<Integer> getSelectedPlaylists() { return selectedPlaylists; }
	    public void setSelectedPlaylists(List<Integer> selectedPlaylists) { this.selectedPlaylists = selectedPlaylists; }

	    public List<Integer> getSelectedSubplaylists() { return selectedSubplaylists; }
	    public void setSelectedSubplaylists(List<Integer> selectedSubplaylists) { this.selectedSubplaylists = selectedSubplaylists; }

	    public List<Integer> getSelectedScenarios() { return selectedScenarios; }
	    public void setSelectedScenarios(List<Integer> selectedScenarios) { this.selectedScenarios = selectedScenarios; }
}

package in.canaris.cloud.openstack.entity;

import java.util.List;

public class UserWisePlaylistForm {

	 private Long userId;              // selected user
		private List<Integer> playlistIds; // selected playlists
		private List<Integer> subplaylistIds; // selected playlists
		private List<Integer> scenarioIds; // selected playlists
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public List<Integer> getPlaylistIds() {
			return playlistIds;
		}
		public void setPlaylistIds(List<Integer> playlistIds) {
			this.playlistIds = playlistIds;
		}
		public List<Integer> getSubplaylistIds() {
			return subplaylistIds;
		}
		public void setSubplaylistIds(List<Integer> subplaylistIds) {
			this.subplaylistIds = subplaylistIds;
		}
		public List<Integer> getScenarioIds() {
			return scenarioIds;
		}
		public void setScenarioIds(List<Integer> scenarioIds) {
			this.scenarioIds = scenarioIds;
		}

	  
	    
	}

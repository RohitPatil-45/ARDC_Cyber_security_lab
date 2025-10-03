package in.canaris.cloud.openstack.entity;

import java.util.List;
import java.util.stream.Collectors;

public class SubjectWisePlaylistView {
	 private Integer subjectId;
	    private String subjectName;
	    private Integer departmentId;
	    private String departmentName;
	    private Integer courseId;
	    private String courseName;
	    private Integer semesterId;
	    private String semesterName;
	    private List<Playlist> playlists;
	    private List<SubPlaylist> subplaylists;
	    private List<Add_Scenario> scenarios;
	    
	    // Constructors
	    public SubjectWisePlaylistView() {}
	    
	    // Getters and Setters
	    public Integer getSubjectId() { return subjectId; }
	    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }
	    
	    public String getSubjectName() { return subjectName; }
	    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
	    
	    public Integer getDepartmentId() { return departmentId; }
	    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
	    
	    public String getDepartmentName() { return departmentName; }
	    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
	    
	    public Integer getCourseId() { return courseId; }
	    public void setCourseId(Integer courseId) { this.courseId = courseId; }
	    
	    public String getCourseName() { return courseName; }
	    public void setCourseName(String courseName) { this.courseName = courseName; }
	    
	    public Integer getSemesterId() { return semesterId; }
	    public void setSemesterId(Integer semesterId) { this.semesterId = semesterId; }
	    
	    public String getSemesterName() { return semesterName; }
	    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }
	    
	    public List<Playlist> getPlaylists() { return playlists; }
	    public void setPlaylists(List<Playlist> playlists) { this.playlists = playlists; }
	    
	    public List<SubPlaylist> getSubplaylists() { return subplaylists; }
	    public void setSubplaylists(List<SubPlaylist> subplaylists) { this.subplaylists = subplaylists; }
	    
	    public List<Add_Scenario> getScenarios() { return scenarios; }
	    public void setScenarios(List<Add_Scenario> scenarios) { this.scenarios = scenarios; }
	    
	    // Helper methods for DataTable
	    public String getPlaylistNames() {
	        if (playlists == null || playlists.isEmpty()) return "None";
	        return playlists.stream().map(Playlist::getPlaylistName).collect(Collectors.joining(", "));
	    }
	    
	    public String getSubplaylistNames() {
	        if (subplaylists == null || subplaylists.isEmpty()) return "None";
	        return subplaylists.stream().map(SubPlaylist::getPlaylistName).collect(Collectors.joining(", "));
	    }
	    
	    public String getScenarioNames() {
	        if (scenarios == null || scenarios.isEmpty()) return "None";
	        return scenarios.stream().map(Add_Scenario::getScenarioName).collect(Collectors.joining(", "));
	    }
	    
	    public Integer getPlaylistCount() {
	        return playlists != null ? playlists.size() : 0;
	    }
	    
	    public Integer getSubplaylistCount() {
	        return subplaylists != null ? subplaylists.size() : 0;
	    }
	    
	    public Integer getScenarioCount() {
	        return scenarios != null ? scenarios.size() : 0;
	    }
	}
